package com.home.cipher;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LeaveGroup extends AppCompatActivity implements SharedVariable.DataSnapshotListener {

    private LinearLayout parentContainer;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_group);

        SharedVariable sharedVariable = new SharedVariable(this);
        sharedVariable.addDataSnapshotListener(this);

        common.startLoading(this, "Loading");
        loadGroups();
    }

    private void loadGroups() {
        parentContainer = findViewById(R.id.group_list);
        inflater = LayoutInflater.from(this);

        DataSnapshot snapshot = common.snapshot.child("group");
        parentContainer.removeAllViews();
        for (DataSnapshot snap : snapshot.getChildren()) {
            if (snap.child("user/" + common.rDBEmail).exists())
                loadGroup(String.valueOf(snap.getKey()));
        }
        common.stopLoading();
    }

    private void loadGroup(String groupName) {
        LinearLayout xmlView = (LinearLayout) inflater.inflate(R.layout.one_group, parentContainer, false);
        TextView group = xmlView.findViewById(R.id.my_group);

        group.setText(groupName);
        String name = common.rDBEmail;
        group.setOnClickListener(v -> new AlertDialog.Builder(LeaveGroup.this)
                .setTitle("Confirm")
                .setMessage("Do you sure you want leave from " + groupName + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    common.startLoading(LeaveGroup.this, "Leaving");
                    if (common.isNetworkConnected(LeaveGroup.this)) {
                        DatabaseReference Data = FirebaseDatabase.getInstance().getReference();
                        FirebaseApp.initializeApp(LeaveGroup.this);
                        removeFromTask(groupName);
                        removeFromMeet(groupName);
                        Data.child("server/" + common.server + "/group/" + groupName + "/user/" + name).removeValue().addOnSuccessListener(unused -> {
                            common.stopLoading();
                            Toast.makeText(LeaveGroup.this, "Successfully leaved from " + groupName, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, LeaveGroup.class));
                            finish();
                        }).addOnFailureListener(e -> {
                            common.stopLoading();
                            Toast.makeText(LeaveGroup.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                        });
                    }
                })
                .setNegativeButton("No", null)
                .show());

        parentContainer.addView(xmlView);
    }

    private void removeFromTask(String groupName) {
        DatabaseReference Data = FirebaseDatabase.getInstance().getReference();
        FirebaseApp.initializeApp(this);
        for (DataSnapshot id : common.snapshot.child("task").getChildren()) {
            if (id.child("groups/" + groupName + "/" + common.rDBEmail).exists()) {
                Data.child("server/" + common.server + "/task/" + id.getKey() + "/groups/" + groupName + "/" + common.rDBEmail).removeValue();
            }
        }
    }

    private void removeFromMeet(String groupName) {
        DatabaseReference Data = FirebaseDatabase.getInstance().getReference();
        FirebaseApp.initializeApp(this);
        for (DataSnapshot id : common.snapshot.child("meet").getChildren()) {
                if (id.child("groups/" + groupName + "/" + common.rDBEmail).exists()) {
                    Data.child("server/" + common.server + "/meet/" + id.getKey() + "/groups/" + groupName + "/" + common.rDBEmail).removeValue();
                }
        }
    }

    public void onBackPressed() {
        startActivity(new Intent(this, Home.class));
        finish();
    }
    @Override
    public void onDataSnapshotChanged(String dataSnapshot) {

    }
}