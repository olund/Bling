package com.bling.app.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bling.app.R;
import com.bling.app.app.BlingApp;
import com.bling.app.helper.Constant;
import com.bling.app.helper.Message;

import org.json.JSONException;
import org.json.JSONObject;

public class AddFriendActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private EditText mUsername;
    private Button mAddButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        final String user = intent.getStringExtra("mUser");

        mUsername = (EditText) findViewById(R.id.username);
        mUsername.clearFocus();

        mAddButton = (Button) findViewById(R.id.add_button);

        /*mUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mUsername.setHint(R.string.prompt_friend);
                }
                if (hasFocus) {
                    mUsername.setHint("");
                }
            }
        });*/


        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsername.setError(null);
                String username = mUsername.getText().toString();
                if (username.contains(" ")) {
                    username = username.replace(" ", "");
                    mUsername.setText(username);
                }

                View focusView = null;
                boolean cancel = false;


                if (TextUtils.isEmpty(username)) {
                    mUsername.setError(getString(R.string.error_field_required));
                    cancel = true;
                }

                if (cancel) {
                    mUsername.requestFocus();
                } else {
                    sendFriendRequest(username.toLowerCase(), user);
                }
            }
        });

    }

    private void sendFriendRequest(String username, String user) {
        String url = Constant.URL_MESSAGES + username;

        // Create JSON object to SEND.
        JSONObject obj = new JSONObject();
        try {
            obj.put("fromId", user);
            obj.put("type", Constant.MESSAGE_TYPE_FRIEND_REQUEST);
            obj.put("latitude", 1.1);
            obj.put("longitude", 1.1);
            obj.put("read", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Send HTTP POST with JSON object.
        JsonObjectRequest req = new JsonObjectRequest(url, obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.i(TAG, response.toString(4));
                    if (response.has("err")) {
                        mUsername.setError(getString(R.string.error_user_not_found));
                        mUsername.requestFocus();
                    } else {
                        Log.d(TAG, "Friend request sent");
                        finish();
                    }
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
