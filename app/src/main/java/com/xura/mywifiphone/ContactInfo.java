package com.xura.mywifiphone;

import android.os.Parcel;
import android.os.Parcelable;

public class ContactInfo implements Parcelable {
    private String name;
    private String phone;
    private int defaultBackground;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public int getDefaultBackground() {
        return defaultBackground;
    }
    public void setDefaultBackground(int defaultBackground) {
        this.defaultBackground = defaultBackground;
    }

    public static final Parcelable.Creator<ContactInfo> CREATOR = new Creator<ContactInfo>() {
        public ContactInfo createFromParcel(Parcel source) {
            ContactInfo contact = new ContactInfo();
            contact.name = source.readString();
            contact.phone = source.readString();
            contact.defaultBackground = source.readInt();
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
        parcel.writeString(phone);
        parcel.writeInt(defaultBackground);
    }
}
