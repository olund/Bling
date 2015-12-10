package com.bling.app.activity;

import android.content.Intent;
import android.location.Location;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bling.app.R;
import com.bling.app.app.BlingApp;
import com.bling.app.helper.Constant;
import com.bling.app.helper.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class DistanceActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private Button mDistanceButton;
    private Button mSendBack;
    private TextView mFromText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        final Message message = (Message) intent.getParcelableExtra("message");

        mDistanceButton = (Button) findViewById(R.id.distance);
        mSendBack = (Button) findViewById(R.id.bling_back);

        mFromText = (TextView) findViewById(R.id.user);

        final Location curr = (Location) intent.getParcelableExtra("currentLocation");
        final String user = intent.getStringExtra("mUser");

        mDistanceButton.setText(calculateDistance(curr, message.location));
        mFromText.setText(message.from + " is");


        mSendBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendDistanceRequest(message, curr, user);
                finish();
            }
        });

    }

    private void sendDistanceRequest(Message message, Location curr, String user) {
        String URL = Constant.URL_MESSAGES;

        // Create JSON object to SEND.
        JSONObject obj = new JSONObject();
        try {
            obj.put("fromId", user);
            obj.put("toId", message.fromId);
            obj.put("type", Constant.MESSAGE_TYPE_DISTANCE);
            obj.put("latitude", curr.getLatitude());
            obj.put("longitude", curr.getLongitude());
            obj.put("read", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Send HTTP POST with JSON object.
        JsonObjectRequest req = new JsonObjectRequest(URL, obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.i(TAG, response.toString(4));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.getMessage());
            }
        });

        // Adding request to request queue
        BlingApp.getInstance().addToRequestQueue(req);
    }


    private String calculateDistance(Location currentLocation, Location messageLocation) {

        float distanceBetween = currentLocation.distanceTo(messageLocation);

        if (distanceBetween < 1000) {
            return (int)distanceBetween + "m";
        }

        if (distanceBetween < 1000000) {
            distanceBetween /= 1000;
            if (distanceBetween > 99) {
                return (int) distanceBetween + "km";
            }

            return String.format("%.1f", distanceBetween) + "km";
        }

        if (distanceBetween < 10000) {
            distanceBetween /= 10;

            return (int) distanceBetween + "miles";
        }

        return ">999 miles";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
