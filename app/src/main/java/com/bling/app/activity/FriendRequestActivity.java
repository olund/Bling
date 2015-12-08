package com.bling.app.activity;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

public class FriendRequestActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    private Button mAcceptButton;
    private Button mFromButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Message message = (Message) intent.getParcelableExtra("message");

        mAcceptButton = (Button) findViewById(R.id.accept);

        mFromButton = (Button) findViewById(R.id.user);

        mFromButton.setText(message.from);

        mAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }

    private void sendFriendRequest(Message message, Location curr) {
        String URL = "http://192.168.1.210:3000/messages"; // TODO: FIX

        // Create JSON object to SEND.
        JSONObject obj = new JSONObject();
        try {
            obj.put("fromId", message.fromId); // TODO: FIX
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
