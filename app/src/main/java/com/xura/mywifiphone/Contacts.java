
/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.xura.mywifiphone;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.graphics.BitmapCompat;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;


public class Contacts {


    private final Random RANDOM = new Random();
    private Hashtable<String, Hashtable> contactDic = new Hashtable<>();
    private Colors colors = new Colors();
    private LruCache<String, Bitmap> mMemoryCache;
    public Context context;
    private Bitmap default_icon;

    public Contacts (Context activity_context) {
        mMemoryCache = new LruCache<>(1048576); // 1MB
        context = activity_context;
        default_icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_person_white_48dp);
    }


    public int getDrawableBackground(String contactName) {
        int res = R.drawable.city;
        /*
        if (contactDic.containsKey(contactName)) {
            Hashtable<String, String> dic = contactDic.get(contactName);
            if (dic.containsKey("photo")) {

            }
        }
        */
        return res;
    }
    public int getDrawableFace(int position) {
        return R.drawable.face;
    }

    // Get Most called contacts
    public List<String> readMostContactedNames() {
        ContentResolver cr = context.getContentResolver();
        String[] columns = {ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.TIMES_CONTACTED,
                ContactsContract.Contacts.LAST_TIME_CONTACTED,
                ContactsContract.Contacts.STARRED};
        String orderedBy = ContactsContract.Contacts.TIMES_CONTACTED + " DESC, "
                            + ContactsContract.Contacts.LAST_TIME_CONTACTED + " DESC, "
                            + ContactsContract.Contacts.SORT_KEY_PRIMARY + " ASC";
        Cursor cur = cr.query( ContactsContract.Contacts.CONTENT_URI,columns, null, null,orderedBy);

        int amount = 0;
        if (cur != null) {
            amount = cur.getCount();
        }
        ArrayList<String> list = new ArrayList<>(amount);
        ArrayList<String> listStarred = new ArrayList<>(amount);

        if (amount > 0) {
            while (cur.moveToNext()) {
                Hashtable<String, String> dic;

                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                long contactId = cur.getLong(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                long last_time =  cur.getLong(cur.getColumnIndex(ContactsContract.Contacts.LAST_TIME_CONTACTED));
                int starred =  cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.STARRED));
                if (contactDic.containsKey(name)) {
                    dic = contactDic.get(name);
                } else {
                    dic = new Hashtable<>();
                }
                if (last_time != 0) {
                    dic.put("last", String.valueOf(last_time));
                } else {
                    dic.put("last", "0");
                }
                if (starred != 0) {
                    listStarred.add(name);
                    dic.put("starred", "yes");
                } else {
                    list.add(name);
                    dic.put("starred", "no");
                }
                contactDic.put(name, dic);

            }
        }
        if (cur != null) {
            cur.close();
        }
        List<String> newList = new ArrayList<>(listStarred.size() + list.size());
        newList.addAll(listStarred);
        newList.addAll(list);
        return newList;
    }

    // Get Contact List with only name and id
    public List<String> readContactsNames() {
        ContentResolver cr = context.getContentResolver();
        String[] columns = {ContactsContract.Contacts._ID,
                            ContactsContract.Contacts.DISPLAY_NAME,
                            ContactsContract.Contacts.PHOTO_URI,
                            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
                            ContactsContract.Contacts.HAS_PHONE_NUMBER};
        Cursor cur = cr.query(  ContactsContract.Contacts.CONTENT_URI,columns, null, null,
                ContactsContract.Contacts.SORT_KEY_PRIMARY + " ASC");

        int amount = 0;
        if (cur != null) {
            amount = cur.getCount();
        }
        ArrayList<String> list = new ArrayList<>(amount);

        if (amount > 0) {
            while (cur.moveToNext()) {
                Hashtable<String, String> dic;

                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                long contactId = cur.getLong(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                //String photo_id =  cur.getLong(cur.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
                String photo_uri =  cur.getString(cur.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
                String thumbnail_uri =  cur.getString(cur.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));

                if (contactDic.containsKey(name)) {
                    dic = contactDic.get(name);
                } else {
                    dic = new Hashtable<>();
                }
                if (thumbnail_uri != null) {
                    dic.put("thumbnail", thumbnail_uri);
                } else {
                    dic.put("thumbnail", "");
                }
                if (photo_uri != null) {
                    dic.put("photo", photo_uri);
                } else {
                    dic.put("photo", "");
                }
                contactDic.put(name, dic);
                list.add(name);
            }
        }
        if (cur != null) {
            cur.close();
        }

        return list;
    }

    //Get specific contact's Infos
    public void fetchContactBaseInfos(String name) {

        Hashtable<String, String> aContactDic = contactDic.get(name);
        String sID = aContactDic.get("id");

        ContentResolver cr = context.getContentResolver();
        String query_id = ContactsContract.Contacts._ID+"="+sID;
        String[] columns = {ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_URI,
                ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
                ContactsContract.Contacts.HAS_PHONE_NUMBER};
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, columns, query_id, null, null);

        int amount = 0;
        if (cur != null) {
            amount = cur.getCount();
        }
        ArrayList<String> list = new ArrayList<>(amount);

        if (amount > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                long contactId = cur.getLong(cur.getColumnIndex(ContactsContract.Contacts._ID));

                //String photo_id =  cur.getLong(cur.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
                String photo_uri =  cur.getString(cur.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
                String thumbnail_uri =  cur.getString(cur.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));

                if (thumbnail_uri != null) {
                    aContactDic.put("thumbnail", thumbnail_uri);
                } else {
                    aContactDic.put("thumbnail", "");
                }
                if (photo_uri != null) {
                    aContactDic.put("photo", photo_uri);
                } else {
                    aContactDic.put("photo", "");
                }
                contactDic.put(name, aContactDic);

                /*
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {


                    // get the phone number
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phone = pCur.getString(
                                pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        System.out.println("phone" + phone);
                    }
                    pCur.close();


                    // get email and type

                    Cursor emailCur = cr.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (emailCur.moveToNext()) {
                        // This would allow you get several email addresses
                        // if the email addresses were stored in an array
                        String email = emailCur.getString(
                                emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        String emailType = emailCur.getString(
                                emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

                        System.out.println("Email " + email + " Email Type : " + emailType);
                    }
                    emailCur.close();

                    // Get note.......
                    String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] noteWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
                    Cursor noteCur = cr.query(ContactsContract.Data.CONTENT_URI, null, noteWhere, noteWhereParams, null);
                    if (noteCur.moveToFirst()) {
                        String note = noteCur.getString(noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                        System.out.println("Note " + note);
                    }
                    noteCur.close();

                    //Get Postal Address....

                    String addrWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] addrWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
                    Cursor addrCur = cr.query(ContactsContract.Data.CONTENT_URI,
                            null, null, null, null);
                    while(addrCur.moveToNext()) {
                        String poBox = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
                        String street = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                        String city = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                        String state = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
                        String postalCode = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
                        String country = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                        String type = addrCur.getString(
                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));

                        // Do something with these....

                    }
                    addrCur.close();

                    // Get Instant Messenger.........
                    String imWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] imWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE};
                    Cursor imCur = cr.query(ContactsContract.Data.CONTENT_URI,
                            null, imWhere, imWhereParams, null);
                    if (imCur.moveToFirst()) {
                        String imName = imCur.getString(
                                imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
                        String imType;
                        imType = imCur.getString(
                                imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.TYPE));
                    }
                    imCur.close();

                    // Get Organizations.........

                    String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] orgWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
                    Cursor orgCur = cr.query(ContactsContract.Data.CONTENT_URI,
                            null, orgWhere, orgWhereParams, null);
                    if (orgCur.moveToFirst()) {
                        String orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
                        String title = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
                    }
                    orgCur.close();

                }*/
            }
        }
        if (cur != null) {
            cur.close();
        }
    }

    // Set Favorites background color based on random choice
    public void setFavoriteBackground(LinearLayout fav_layout, String contactName) {
        Hashtable<String, String> dic = contactDic.get(contactName);
        int aColor;
        if (dic.containsKey("fav_color")) {
            aColor = Integer.parseInt(dic.get("fav_color"));
            Log.d("DEBUG", contactName+" has saved color "+colors.getColorName(aColor));
        } else {
            Log.d("DEBUG", contactName+" does not have a fav_color");
            aColor = colors.getRandomColor();
            dic.put("fav_color", String.valueOf(aColor));
            contactDic.put(contactName, dic);
            Log.d("DEBUG", contactName+" has now color "+colors.getColorName(aColor));
        }
        fav_layout.setBackgroundColor(aColor);
    }


    ///////////////////////////////////////////////////////////////////////////////
    // This part is dedicated to Thumbnails generation for the receicler view.   //
    // There are provided within an asynchronous task so as to not block the app //
    ///////////////////////////////////////////////////////////////////////////////

    // Main function called by ContactFragment
    public void setContactThumbnail(ImageView mImageView, String contactName) {
        //fetchContactBaseInfos(contactName);

        if (hasThumbnail(contactName)) {
            Glide.with(mImageView.getContext())
                    .load(Uri.parse(getThumbnail(contactName)))
                    .fitCenter()
                    .into(mImageView);
        } else {
            loadBitmap(mImageView, contactName);
        }
    }

    // Check if there is a thimbnail for a contact
    public Boolean hasThumbnail(String name) {
        Boolean test = Boolean.FALSE;
        if (contactDic.get(name).get("thumbnail") != "") {
            test = Boolean.TRUE;
        }
        return test;
    }

    private String getThumbnail(String name) {
        String photoUri = contactDic.get(name).get("thumbnail").toString();
        return photoUri;
    }


    // Otherwise create a round bitmap with the uppercase of the first letter
    private Bitmap generateCircleBitmap(int circleColor, float diameterDP, String text){
        final int textColor = 0xffffffff;

        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float diameterPixels = diameterDP * (metrics.densityDpi / 160f);
        float radiusPixels = diameterPixels/2;

        // Create the bitmap
        Bitmap output = Bitmap.createBitmap((int) diameterPixels, (int) diameterPixels,
                Bitmap.Config.ARGB_8888);

        // Create the canvas to draw on
        Canvas canvas = new Canvas(output);
        canvas.drawARGB(0, 0, 0, 0);

        // Draw the circle
        final Paint paintC = new Paint();
        paintC.setAntiAlias(true);
        paintC.setColor(circleColor);
        canvas.drawCircle(radiusPixels, radiusPixels, radiusPixels, paintC);

        // Draw the text
        if (text != null && text.length() > 0) {
            final Paint paintT = new Paint();
            paintT.setColor(textColor);
            paintT.setAntiAlias(true);
            //paintT.setTextSize(radiusPixels * 2);
            paintT.setTextSize(Math.round(radiusPixels * 1.5));
            //Typeface typeFace = Typeface.createFromAsset(context.getAssets(),"fonts/Roboto-Thin.ttf");
            //paintT.setTypeface(typeFace);
            final Rect textBounds = new Rect();
            paintT.getTextBounds(text, 0, text.length(), textBounds);
            canvas.drawText(text, radiusPixels - textBounds.exactCenterX(), radiusPixels - textBounds.exactCenterY(), paintT);
        }


        int bitmapByteCount= BitmapCompat.getAllocationByteCount(output);
        return output;
    }

    // Save generated round bitmaps for all name beginin with the same letter
    private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }
    private Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    // Load a round colored bitmap with capital letter
    private void loadBitmap(ImageView mImageView, String contactName) {
        String firstLetter = contactName.substring(0, 1).toUpperCase();
        int aColor = colors.getRandomColor();

        if (cancelPotentialWork(aColor, mImageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(mImageView, firstLetter);
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(context.getResources(), default_icon, task);
            mImageView.setImageDrawable(asyncDrawable);
            task.execute(aColor);
        }

    }

    // The following part is dedicated to the asynchrounous worker queu
    // cf http://developer.android.com/training/displaying-bitmaps/process-bitmap.html
    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private String key;
        private int aColor;

        public BitmapWorkerTask(ImageView imageView, String firstLetter) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
            key = firstLetter;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            aColor = params[0];
            Bitmap abitmap = getBitmapFromMemCache(key);
            if (abitmap == null) {
                abitmap = generateCircleBitmap(aColor, 40, key);
                addBitmapToMemoryCache(key, abitmap);
            }
            return abitmap;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }
    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                             BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }
    public static boolean cancelPotentialWork(int data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final int bitmapData = bitmapWorkerTask.aColor;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData == 0 || bitmapData != data) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }
    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

}


