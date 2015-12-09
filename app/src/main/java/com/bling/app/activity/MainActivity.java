package com.bling.app.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.bling.app.R;
import com.bling.app.fragments.FriendsFragment;
import com.bling.app.fragments.HistoryFragment;
import com.bling.app.fragments.NearbyFragment;
import com.bling.app.helper.Constant;
import com.bling.app.helper.LocationModel;
import com.bling.app.helper.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity implements LocationModel.OnCustomStateListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    static final int PERMISSION_REQUEST_LOCATION = 201;
    public boolean locationPermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocationModel.getInstance().setListener(this);
        LocationModel.getInstance().setContext(this);

        //checkPermissions();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void locationChanged() {
        Location location = LocationModel.getInstance().getLocation();
        Log.d(TAG, "MainActivity says: Location changed: " + String.valueOf(location));
        Snackbar.make(this.findViewById(android.R.id.content), "Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude(), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    locationPermission = true;


                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /*
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            SharedPreferences sharedPref = getSharedPreferences(Constant.USER_PREFS, 0);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Constant.USERNAME, "");
            editor.putString(Constant.USER_ID, "");
            editor.commit();

            Intent intent = new Intent(this, SplashActivity.class);
            startActivity(intent);
            finish();

        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_login) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_register) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Log.e(TAG, "NOT GRANTED");

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            Log.e(TAG, "GRANTED");
            locationPermission = true;
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPager.setPageMargin(40);
        viewPager.setPageMarginDrawable(R.drawable.view_pager_margin);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FriendsFragment(), "FRIENDS");
        adapter.addFragment(new HistoryFragment(), "HISTORY");
        adapter.addFragment(new NearbyFragment(), "NEARBY");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);

    }
}