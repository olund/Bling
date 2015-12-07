package com.bling.app.helper;

import android.text.format.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Message implements Serializable {

    public JSONObject message;

    public String id;
    public String fromId;
    public String from;
    public String type;
    public long created;
    public double longitude;
    public double latitude;
    public boolean read;

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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getAge() {
        // TODO: Remove dot from Date format
        return (String) DateUtils.getRelativeTimeSpanString(this.created, System.currentTimeMillis(), 0L, DateUtils.FORMAT_ABBREV_ALL);
    }
}
