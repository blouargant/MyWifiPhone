package com.xura.mywifiphone;

import android.os.Parcel;
import android.os.Parcelable;

public class ContactInfo implements Parcelable {
    private String name;
    private String phone;
    private String photo;
    private int color;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getColor() {
        return color;
    }
    public void setColor(int color) {
        this.color = color;
    }


    public static final Parcelable.Creator<ContactInfo> CREATOR = new Creator<ContactInfo>() {
        public ContactInfo createFromParcel(Parcel source) {
            ContactInfo contact = new ContactInfo();
            contact.name = source.readString();
            contact.color = source.readInt();
            return contact;
        }
        public ContactInfo[] newArray(int size) {
            return new ContactInfo[size];
        }
    };

    public int describeContents() {
        return 0;
    }
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(name);
        parcel.writeInt(color);
    }
}
