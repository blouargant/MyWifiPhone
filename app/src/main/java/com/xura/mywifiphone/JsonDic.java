package com.xura.mywifiphone;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by bertrand on 12/02/16.
 */
public class JsonDic implements Parcelable {
    private String serial;
    private JSONObject jsonDic;
    public ArrayList<String> keys = new ArrayList<>();

    public JsonDic() {
        jsonDic = new JSONObject();
        JSONArray entries = new JSONArray();
        JSONArray keys = new JSONArray();
    }

    public ArrayList<String> getKeys() {
        ArrayList<String> key_list = new ArrayList<>();
        try {
            JSONArray joKeys = jsonDic.getJSONArray("keys");
            for (int i=0; i<joKeys.length(); i++) {
                key_list.add(joKeys.optString(i));
            }
        } catch (JSONException e) {
            System.out.println(e);
        }
        return key_list;
    }

    public void serialyse() {
    }

    // Add a new string, if key already defined then it is replaced
    public void putString(String key, String value) {
        try {
            JSONArray joEntries = jsonDic.getJSONArray("entries");
            JSONArray joKeys = jsonDic.getJSONArray("keys");
            JSONObject newEntry = new JSONObject();
            newEntry.put(key, value);
            joEntries.put(newEntry);
            if (! this.keys.contains(key)) {
                this.keys.add(key);
                joKeys.put(key);
            }
            jsonDic.put("entries", joEntries);
            jsonDic.put("keys", joKeys);

        } catch (JSONException e) {
            System.out.println(e);
        }

    }


    public static final Parcelable.Creator<JsonDic> CREATOR = new Creator<JsonDic>() {
        public JsonDic createFromParcel(Parcel source) {
            JsonDic dic = new JsonDic();
            dic.serial = source.readString();
            return dic;
        }
        public JsonDic[] newArray(int size) {
            return new JsonDic[size];
        }
    };

    public int describeContents() {
        return 0;
    }
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(serial);
    }
}
