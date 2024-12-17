package com.home.cipher;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.os.Handler;
import android.os.Looper;

public class Welcome extends AppCompatActivity implements SharedVariable.DataSnapshotListener {

    private static final int CHECK_INTERVAL = 100; // Adjust the interval as needed
    private Handler handler;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Intent serviceIntent = new Intent(this, MyService.class);
        this.startService(serviceIntent);
        common.tips  = new SharedVariable(this).getTips();
        try {
            if (new SharedVariable(this).getIsLogIn()) {
                if(new SharedVariable(this).getServer().equals("null")){
                    startActivity(new Intent(this, Sever.class));
                }else {
                    common.startLoading(this, "Connecting to Database..");
                    handler = new Handler(Looper.getMainLooper());
                    // Wait and check again
                    Runnable checkSnapshotRunnable = new Runnable() {
                        @Override
                        public void run() {
                            if (common.snapshot != null) {
                                startActivity(new Intent(Welcome.this, Home.class));
                            } else {
                                // Wait and check again
                                handler.postDelayed(this, CHECK_INTERVAL);
                            }
                        }
                    };

                    // Start checking for snapshot
                    handler.post(checkSnapshotRunnable);
                }
            }
        } catch (Exception e) {
            Log.d("HomeStartError", String.valueOf(e));
        }
    }

    public void openRegistration(View view) {
        startActivity(new Intent(this, SignIn.class));
    }

    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit App")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> finishAffinity())
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onDataSnapshotChanged(String dataSnapshot) {

    }
}