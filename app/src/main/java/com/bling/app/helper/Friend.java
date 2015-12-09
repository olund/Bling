package com.bling.app.helper;

import android.os.Parcel;
import android.os.Parcelable;

public class Friend implements Parcelable {
    public String id;
    public String username;

    public Friend(String id, String username) {
        this.id = id;
        this.username = username;
    }

    protected Friend(Parcel in) {
        id = in.readString();
        username = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(username);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Friend> CREATOR = new Parcelable.Creator<Friend>() {
        @Override
        public Friend createFromParcel(Parcel in) {
            return new Friend(in);
        }

        @Override
        public Friend[] newArray(int size) {
            return new Friend[size];
        }
    };
}