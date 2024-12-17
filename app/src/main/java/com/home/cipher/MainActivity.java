package com.home.cipher;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements SharedVariable.DataSnapshotListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onDataSnapshotChanged(String dataSnapshot) {

    }
}