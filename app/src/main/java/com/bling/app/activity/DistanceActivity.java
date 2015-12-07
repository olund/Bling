package com.bling.app.activity;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.TextView;

import com.bling.app.R;
import com.bling.app.helper.Message;

public class DistanceActivity extends AppCompatActivity {

    private TextView mDistanceText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Message message = (Message) intent.getParcelableExtra("message");

        mDistanceText = (TextView) findViewById(R.id.distance);

        Location curr = (Location) intent.getParcelableExtra("currentLocation");

        mDistanceText.setText(calculateDistance(curr, message.location));
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
}
