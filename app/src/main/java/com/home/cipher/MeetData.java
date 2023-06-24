package com.home.cipher;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;

public class MeetData extends AppCompatActivity {

    private String link;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meet_data);
        common.startLoading(this, "Loading");
        loadData();
    }

    private void loadData() {
        TextView name, discription, date, time, notifyDate, notifyTime;
        name = findViewById(R.id.Name);
        discription = findViewById(R.id.Description);
        date = findViewById(R.id.Date);
        time = findViewById(R.id.Time);
        notifyDate = findViewById(R.id.Notify_date);
        notifyTime = findViewById(R.id.Notify_time);

        if (common.isNetworkConnected(this)) {
            FirebaseApp.initializeApp(this);
            DatabaseReference Data = FirebaseDatabase.getInstance().getReference();
            Data.child("meet/" + common.dataID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    name.setText(String.valueOf(snapshot.child("name").getValue()));
                    discription.setText(String.valueOf(snapshot.child("description").getValue()));

                    link = String.valueOf(snapshot.child("link").getValue());
                    String[] dayTime;
                    try {
                        dayTime = common.convertToDayTime(common.dataID);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    date.setText(dayTime[0]);
                    time.setText(dayTime[1]);

                    String dt = String.valueOf(snapshot.child("notify").getValue());
                    try {
                        notifyDate.setText(common.convertToDate(dt));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        notifyTime.setText(common.convertToTime(dt));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    common.stopLoading();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    common.stopLoading();
                }
            });
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    public void goToMeeting(View view) {
        Uri webpage = Uri.parse(link);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Check if the YouTube app is installed
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Open the YouTube link in the app
            startActivity(intent);
        } else {
            // If the YouTube app is not installed, open the link in a web browser
            intent.setPackage(null);
            startActivity(intent);
        }
    }

    public void done(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Done Meeting")
                .setMessage("Are you sure this Meeting is complete?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    try {
                        finishTask();
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void finishTask() throws ParseException {
        if (common.isNetworkConnected(this)) {
            FirebaseApp.initializeApp(this);
            DatabaseReference Data = FirebaseDatabase.getInstance().getReference();
            Data.child("meet/" + common.dataID+"/user/"+common.rDBEmail).setValue("done").addOnSuccessListener(unused -> startActivity(new Intent(MeetData.this,Home.class)));
        }
    }

    public void copyLink(View view) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", link);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Link copied to clipboard", Toast.LENGTH_SHORT).show();
    }
}