package com.xura.mywifiphone.Utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by bertrand on 12/02/16.
 */
public class JsonDic implements Parcelable {
    private String serial = "{}";
    private JSONObject jsonDic;
    private ArrayList<String> keys = new ArrayList<>();
    private String TAG = "JsonDic";


    public JsonDic() {
        initJsonDic();
    }
    private void initJsonDic() {
        jsonDic = new JSONObject();
        JSONObject entries = new JSONObject();
        JSONArray keys = new JSONArray();
        try {
            jsonDic.putOpt("entries", entries);
            jsonDic.putOpt("keys", keys);
        } catch (JSONException e) {
            Log.w(TAG, "initJsonDic :" + e);
        }
    }
    private void jsonToSerial() {
        this.serial = jsonDic.toString();
    }
    private void serialToJson(String strDic) {
        try {
            initJsonDic();
            if ((strDic != null) && (strDic != "" )) {
                this.jsonDic = new JSONObject(this.serial);
                this.keys = getKeys();
            }

        } catch (JSONException e) {
            Log.w(TAG, "serialToJson :" + e);
        }
    }
    public void load(String strDic) {
        this.serial = strDic;
        serialToJson(strDic);
    }
    public String toString() {
        this.serial = jsonDic.toString();
        return this.serial;
    }

    private ArrayList<String> getKeys() {
        ArrayList<String> key_list = new ArrayList<>();
        try {
            JSONArray joKeys = jsonDic.getJSONArray("keys");
            for (int i=0; i<joKeys.length(); i++) {
                key_list.add(joKeys.optString(i));
            }
        } catch (JSONException e) {
            Log.w(TAG, "getKeys :" + e);
        }
        return key_list;
    }
    public ArrayList<String> Keys() {
        return this.keys;
    }
    public Boolean containsKey(String key) {
        return this.keys.contains(key);
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
            Log.w(TAG, "put :" + e);
        }
    }

    public void putDic(String key, JsonDic dic) {
        try {
            JSONObject joEntries = jsonDic.getJSONObject("entries");
            JSONArray joKeys = jsonDic.getJSONArray("keys");
            JSONObject ajsondic = dic.jsonDic;
            joEntries.putOpt(key, ajsondic);
            if (!this.keys.contains(key)) {
                this.keys.add(key);
                joKeys.put(key);
            }
            jsonDic.put("entries", joEntries);
            jsonDic.put("keys", joKeys);
        } catch (JSONException e) {
            Log.w(TAG, "putDic :" + e);
        }
    }
    public JsonDic getDic(String key) {
        JsonDic dic = new JsonDic();
        try {
            JSONObject jsondic = jsonDic.getJSONObject("entries").optJSONObject(key);
            dic.jsonDic = jsondic;
            dic.keys = dic.getKeys();
        } catch (JSONException e) {
            Log.w(TAG, "getDic :" + e);
        }
        return dic;
    }

    private Object get(String key) {
        Object value = new Object();
        try {
            value = jsonDic.getJSONObject("entries").opt(key);
        } catch (JSONException e) {
            Log.w(TAG, "get :" + e);
        }
        return value;
    }
    public String getString(String key) {
        String value = "";
        try {
            value = jsonDic.getJSONObject("entries").getString(key);
        } catch (JSONException e) {
            Log.w(TAG, "getString :" + e);
        }
        return value;
    }
    public Integer getInt(String key) {
        int value = 0;
        try {
            value = jsonDic.getJSONObject("entries").getInt(key);
        } catch (JSONException e) {
            Log.w(TAG, "getInt :" + e);
        }
        return value;
    }
    public Long getLong(String key) {
        Long value = new Long(0);
        try {
            value = jsonDic.getJSONObject("entries").getLong(key);
        } catch (JSONException e) {
            Log.w(TAG, "getLong :" + e);
        }
        return value;
    }
    public Boolean getBoolean(String key) {
        Boolean value = false;
        try {
            value = jsonDic.getJSONObject("entries").getBoolean(key);
        } catch (JSONException e) {
            Log.w(TAG, "getBoolean :" + e);
        }
        return value;
    }
    public ArrayList<String> getArray(String key) {
        ArrayList<String> value;
        value = (ArrayList<String>) this.get(key);
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


    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //Call jsonToSerial to set serial
        jsonToSerial();
        dest.writeString(this.serial);
    }

    protected JsonDic(Parcel in) {
        this.serial = in.readString();
        serialToJson(this.serial);
        //serialToJson to set jsonDic
    }

    public static final Creator<JsonDic> CREATOR = new Creator<JsonDic>() {
        public JsonDic createFromParcel(Parcel source) {
            return new JsonDic(source);
        }

        public JsonDic[] newArray(int size) {
            return new JsonDic[size];
        }
    };
}
