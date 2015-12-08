package com.bling.app.activity;

import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.bling.app.R;
import com.bling.app.helper.Message;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class PositionActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Message mMessage;
    private Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position);

        Intent intent = getIntent();
        mMessage = intent.getParcelableExtra("message");

        mLocation = intent.getParcelableExtra("currentLocation");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15.0f));
    }
}
