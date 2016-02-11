package com.xura.mywifiphone;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public class ContactsJson implements Parcelable {
    private String serial;
    private JSONObject contactDic;

    public ContactsJson() {
        JSONObject contacts = new JSONObject();
        JSONArray list = new JSONArray();
        try {
            contactDic.put("contacts", contacts);
            contactDic.put("list", list);
        } catch (JSONException e) {
            System.out.println(e);
        }
    }

    public String getSerial() {
        return serial;
    }
    public void addName(String name) {
        String photo = "";
        String thumbnail = "";
        long last = 0;
        Boolean starred = false;
        String phone = "";
        String address = "";
        int color = 0;
        try {
            JSONObject contacts = contactDic.getJSONObject("contacts");
            if (contacts.optJSONObject(name) == null) {
                JSONObject nameJo = new JSONObject();
                nameJo.put("photo", photo);
                nameJo.put("thumbnail", thumbnail);
                nameJo.put("last", last);
                nameJo.put("starred", starred);
                nameJo.put("phone", phone);
                nameJo.put("address", address);
                nameJo.put("color", color);
                contacts.put(name, nameJo);
                contactDic.put("contacts", contacts);
                JSONArray list = contactDic.getJSONArray("list");
                list.put(name);
                contactDic.put("list", list);
            }
        } catch (JSONException e) {
            System.out.println(e);
        }
    }
    public void removeName(String name) {
        try {
            JSONObject contacts = contactDic.getJSONObject("contacts");
            if (contacts.optJSONObject(name) != null) {
                contacts.remove(name);
                contactDic.put("contacts", contacts);
                JSONArray list = contactDic.getJSONArray("list");
                for (int i=0; i<list.length(); i++) {
                    if (list.get(i) == name) {
                        list.remove(i);
                        break;
                    }
                }
                contactDic.put("list", list);
            }
        } catch (JSONException e) {
            System.out.println(e);
        }
    }
    public void setPhoto(String name, String photo) {
        try {
            JSONObject aContact;
            JSONObject contacts = contactDic.getJSONObject("contacts");
            aContact = contacts.optJSONObject(name);
            if (aContact == null) {
                addName(name);
                aContact = contacts.optJSONObject(name);
            }
            aContact.put("photo", photo);
            contacts.put(name, aContact);
            contactDic.put("contacts", contacts);

        } catch (JSONException e) {
            System.out.println(e);
        }
    }
    public String getPhoto(String name) {
        String photo = "";
        try {
            JSONObject contacts = contactDic.getJSONObject("contacts");
            JSONObject aContact = contacts.optJSONObject(name);
            if (aContact == null) {
                return photo;
            }
            photo = aContact.optString("photo");
        } catch (JSONException e) {
            System.out.println(e);
        }
        return photo;
    }

    public void setSerial() {
    }

    public static final Parcelable.Creator<ContactsJson> CREATOR = new Creator<ContactsJson>() {
        public ContactsJson createFromParcel(Parcel source) {
            ContactsJson contacts = new ContactsJson();
            contacts.serial = source.readString();
            return contacts;
        }
        public ContactsJson[] newArray(int size) {
            return new ContactsJson[size];
        }
    };

    public int describeContents() {
        return 0;
    }
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(serial);
    }
}
