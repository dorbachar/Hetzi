package com.example.hetzi_beta;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jakewharton.threetenabp.AndroidThreeTen;

public class ToolbarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar);

        AndroidThreeTen.init(this);
    }
}
