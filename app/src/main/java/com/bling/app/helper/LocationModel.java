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
        void stateChanged();
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

        //if (mLocationPermission)
        //
    }

    public Location getLocation() {
        return mLastLocation;
    }

    private void notifyStateChange() {
        for (int i = 0; i < mListenerArray.size(); i++) {
            mListenerArray.get(i).stateChanged();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.e(TAG, "Google Play services connected.");
        buildLocationRequestObject();
        requestLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "Google Play services suspended.");
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
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(30 * 1000)        // 30 seconds, in milliseconds
                .setFastestInterval(10 * 1000);  // 10 seconds, in milliseconds

    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        String latitude = String.valueOf(location.getLatitude());
        String longitude = String.valueOf(location.getLongitude());
        notifyStateChange();
    }

    public void requestLocation() {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Log.d(TAG, "Time since last location update: " + String.valueOf(mLastLocation.getTime()));
            handleNewLocation(mLastLocation);
        } else {
            Log.e(TAG, "No saved location requesting new.");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }
}