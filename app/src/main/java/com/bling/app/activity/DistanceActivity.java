package com.bling.app.activity;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.bling.app.R;
import com.bling.app.helper.Message;

public class DistanceActivity extends AppCompatActivity {

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
        Message message = (Message) intent.getParcelableExtra("message");

        mDistanceButton = (Button) findViewById(R.id.distance);

        mDistanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDistanceButton.setElevation(0);
            }
        });

        mFromText = (TextView) findViewById(R.id.user);

        Location curr = (Location) intent.getParcelableExtra("currentLocation");

        mDistanceButton.setText(calculateDistance(curr, message.location));
        mFromText.setText(message.from + " is");
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
