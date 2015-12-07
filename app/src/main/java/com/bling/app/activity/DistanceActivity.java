package com.bling.app.activity;

import android.content.Intent;
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
        Message message = (Message)intent.getSerializableExtra("message");

        mDistanceText = (TextView) findViewById(R.id.distance);
        mDistanceText.setText("test");
    }
}
