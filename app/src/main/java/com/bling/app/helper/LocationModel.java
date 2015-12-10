package com.bling.app.helper;


import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;

import com.bling.app.activity.MainActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class LocationModel extends Application implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String TAG = LocationModel.class.getSimpleName();


    private Context context;
    protected GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public Location mLastLocation;
    private boolean mLocationPermission = false;

    public interface OnCustomStateListener {
        void locationChanged();
    }

    private static LocationModel mInstance;
    private ArrayList<OnCustomStateListener> mListenerArray = new ArrayList<>();
    private boolean mState;

    private LocationModel() {
    }

    public static LocationModel getInstance() {
        if(mInstance == null) {
            mInstance = new LocationModel();
        }
        return mInstance;
    }

    public void setListener(OnCustomStateListener listener) {
        mListenerArray.add(listener);
    }

    public void setContext(Context context) {
        this.context = context;
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    public void permissionState(boolean state) {
        mLocationPermission = state;

        Log.d(TAG, "Location permission set to" + state);
        if (mLocationPermission) {
            buildLocationRequestObject();
        }
    }

    public Location getLocation() {
        return mLastLocation;
    }

    private void notifyStateChange() {
        for (int i = 0; i < mListenerArray.size(); i++) {
            mListenerArray.get(i).locationChanged();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Google Play services connected.");
        if (mLocationPermission) {
            buildLocationRequestObject();
            requestLocation();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Google Play services suspended.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Google Play services connection failed.");
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this.context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void buildLocationRequestObject() {
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                //.setInterval(30 * 1000)        // 30 seconds, in milliseconds
                //.setFastestInterval(10 * 1000);  // 10 seconds, in milliseconds

    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        String latitude = String.valueOf(location.getLatitude());
        String longitude = String.valueOf(location.getLongitude());
        notifyStateChange();
    }

    public void requestLocation() {
        if (mLocationPermission) {
            // Get last seen location
            //mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            //Log.e(TAG, "No saved location requesting new.");
            // Request new location
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        } else {
            Log.e(TAG, "No location permission, can't get location.");
        }

    }
}