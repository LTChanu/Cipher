package com.home.cipher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

public class Setting extends AppCompatActivity {

    private ToggleButton tips, reminder;
    private SharedVariable sharedVariable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        reminder = findViewById(R.id.reminder_toggle);
        tips = findViewById(R.id.tips_toggle);
        sharedVariable = new SharedVariable(this);
        loadDate();

        reminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedVariable.setRing(isChecked);
            showRemainder();
        });

        tips.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedVariable.setTips(isChecked);
            common.tips=isChecked;
            showTips();
        });
    }

    private void showTips() {
        if(sharedVariable.getTips())
            Toast.makeText(this, "Tips are enable", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Tips are disable", Toast.LENGTH_SHORT).show();
    }

    private void showRemainder() {
        if(sharedVariable.getRing())
            Toast.makeText(this, "Remainder sound is ON", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Remainder sound is OFF", Toast.LENGTH_SHORT).show();
    }


    private void loadDate() {
        tips.setChecked(sharedVariable.getTips());
        reminder.setChecked(sharedVariable.getRing());
    }

    public void onBackPressed() {
        startActivity(new Intent(this, Home.class));
        finish();
    }
    public void GoToProfile(View view) {
        startActivity(new Intent(this, ChangeName.class));
    }

    public void GoToServer(View view) {
        startActivity(new Intent(this, Sever.class));
    }
}