package com.bling.app.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bling.app.R;
import com.bling.app.app.BlingApp;
import com.bling.app.helper.Constant;
import com.bling.app.helper.Message;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

public class PositionActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String TAG = MainActivity.class.getSimpleName();


    private GoogleMap mMap;
    private Button mSendBack;
    private Message mMessage;
    private Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSendBack = (Button) findViewById(R.id.bling_back);

        Intent intent = getIntent();
        mMessage = intent.getParcelableExtra("message");

        mLocation = intent.getParcelableExtra("currentLocation");
        final String user = intent.getStringExtra("mUser");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mSendBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPositionRequest(mMessage, mLocation, user);
                finish();
            }
        });

    }

    private void sendPositionRequest(Message message, Location curr, String user) {
        String URL = Constant.URL_MESSAGES;

        // Create JSON object to SEND.
        JSONObject obj = new JSONObject();
        try {
            obj.put("fromId", user);
            obj.put("toId", message.fromId);
            obj.put("type", Constant.MESSAGE_TYPE_POSITION);
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


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng location = new LatLng(mMessage.location.getLatitude(), mMessage.location.getLongitude());
        Marker marker = mMap.addMarker(new MarkerOptions().position(location).title(mMessage.from + " is here"));
        marker.showInfoWindow();
        marker.setFlat(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15.0f));
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
