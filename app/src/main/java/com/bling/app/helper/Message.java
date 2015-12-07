package com.bling.app.helper;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;


public class Message implements Parcelable {

    public JSONObject message;

    public String id;
    public String fromId;
    public String from;
    public String type;
    public long created;
    public double longitude;
    public double latitude;
    public boolean read;
    public Location location;

    public Message(JSONObject message) {
        this.message = message;
        try {
            this.id = message.getString("_id");
            this.fromId = message.getJSONObject("fromId").getString("_id");
            this.from = message.getJSONObject("fromId").getString("username");
            this.type = message.getString("type");
            this.created = message.getLong("created");
            this.longitude = message.getDouble("longitude");
            this.latitude = message.getDouble("latitude");
            this.read = message.getBoolean("read");

            this.location = new Location("location");
            this.location.setLatitude(this.latitude);
            this.location.setLongitude(this.longitude);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getAge() {
        // TODO: Remove dot from Date format
        return (String) DateUtils.getRelativeTimeSpanString(this.created, System.currentTimeMillis(), 0L, DateUtils.FORMAT_ABBREV_ALL);
    }

    protected Message(Parcel in) {
        try {
            message = in.readByte() == 0x00 ? null : new JSONObject(in.readString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        id = in.readString();
        fromId = in.readString();
        from = in.readString();
        type = in.readString();
        created = in.readLong();
        longitude = in.readDouble();
        latitude = in.readDouble();
        read = in.readByte() != 0x00;
        location = (Location) in.readValue(Location.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (message == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeString(message.toString());
        }
        dest.writeString(id);
        dest.writeString(fromId);
        dest.writeString(from);
        dest.writeString(type);
        dest.writeLong(created);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeByte((byte) (read ? 0x01 : 0x00));
        dest.writeValue(location);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
}