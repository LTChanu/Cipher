package com.home.cipher;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class Welcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Intent intent = new Intent(this, MyService.class);
        startService(intent);

        try {
            if (new SharedVariable(this).getIsLogIn()) {
                startActivity(new Intent(this, Home.class));
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
}