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
        JSONObject entries = new JSONObject();
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

    // Add a new entry, if key already defined then it is replaced
    public void put(String key, Object value) {
        try {
            if ( (value instanceof Integer) ||
                 (value instanceof String) ||
                 (value instanceof Long) ||
                 (value instanceof Boolean) ) {
                JSONObject joEntries = jsonDic.getJSONObject("entries");
                JSONArray joKeys = jsonDic.getJSONArray("keys");
                joEntries.putOpt(key, value);
                if (!this.keys.contains(key)) {
                    this.keys.add(key);
                    joKeys.put(key);
                }
                jsonDic.put("entries", joEntries);
                jsonDic.put("keys", joKeys);
            } else {
                throw new IllegalArgumentException("Illegal Argument :" + value);
            }
        } catch (JSONException e) {
            System.out.println(e);
        }
    }
    public Object get(String key) {
        Object value = new Object();
        try {
            value = jsonDic.getJSONObject("entries").opt(key);
        } catch (JSONException e) {
            System.out.println(e);
        }
        return value;
    }
    public Object getString(String key) {
        String value = "";
        value = (String) this.get(key);
        return value;
    }
    public Object getInt(String key) {
        Integer value = 0;
        value = (Integer) this.get(key);
        return value;
    }
    public Object getLong(String key) {
        Long value = new Long(0);
        value = (Long) this.get(key);
        return value;
    }
    public Object getBoolean(String key) {
        Boolean value = false;
        value = (Boolean) this.get(key);
        return value;
    }
    public Object getArray(String key) {
        String value = "";
        value = (String) this.get(key);
        return value;
    }

    void foo(String a, Object... b) {

        Integer b1 = 0;
        String b2 = "";
        if (b.length > 0) {
            if (!(b[0] instanceof Integer)) {
                throw new IllegalArgumentException("...");
            }
            b1 = (Integer)b[0];
        }
        if (b.length > 1) {
            if (!(b[1] instanceof String)) {
                throw new IllegalArgumentException("...");
            }
            b2 = (String)b[1];
            //...
        }
        //...
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
