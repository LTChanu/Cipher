package com.home.cipher;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;

public class CiphData extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ciph_data);
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
            Data.child("task/" + common.dataID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    name.setText(String.valueOf(snapshot.child("name").getValue()));
                    discription.setText(String.valueOf(snapshot.child("description").getValue()));

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

    public void done(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Done Task")
                .setMessage("Are you sure this task is complete?")
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
            if(common.checkDate(common.convertToDayTime(common.dataID)[0])==0)
                Data.child("task/" + common.dataID+"/user/"+common.rDBEmail).setValue("late").addOnSuccessListener(unused -> startActivity(new Intent(CiphData.this,Home.class)));
            else
                Data.child("task/" + common.dataID+"/user/"+common.rDBEmail).setValue("done").addOnSuccessListener(unused -> startActivity(new Intent(CiphData.this,Home.class)));
        }
    }
}