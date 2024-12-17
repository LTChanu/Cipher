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

public class AddGroup extends AppCompatActivity implements SharedVariable.DataSnapshotListener {

    private LinearLayout parentContainer;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        common.startLoading(this, "Loading");
        loadGroups();
    }

    private void loadGroups() {
        parentContainer = findViewById(R.id.group_list);
        inflater = LayoutInflater.from(this);

        SharedVariable sharedVariable = new SharedVariable(this);
        sharedVariable.addDataSnapshotListener(this);

        DataSnapshot snapshot = common.snapshot.child("group");
        parentContainer.removeAllViews();
        for (DataSnapshot snap : snapshot.getChildren()) {
            if (!snap.child("user/" + common.rDBEmail).exists() && String.valueOf(snap.child("type").getValue()).equals("public"))
                loadGroup(String.valueOf(snap.getKey()));
        }
        common.stopLoading();
    }

    private void loadGroup(String groupName) {
        LinearLayout xmlView = (LinearLayout) inflater.inflate(R.layout.one_group, parentContainer, false);
        TextView group = xmlView.findViewById(R.id.my_group);

        group.setText(groupName);
        String name = common.rDBEmail;
        group.setOnClickListener(v -> new AlertDialog.Builder(AddGroup.this)
                .setTitle("Confirm")
                .setMessage("Do you sure you want add to " + groupName + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    common.startLoading(AddGroup.this, "Adding");
                    if (common.isNetworkConnected(AddGroup.this)) {
                        DatabaseReference Data = FirebaseDatabase.getInstance().getReference();
                        FirebaseApp.initializeApp(AddGroup.this);
                        addToTask(groupName);
                        addToMeet(groupName);
                        Data.child("server/" + common.server + "/group/" + groupName + "/user/" + name).setValue(name).addOnSuccessListener(unused -> {
                            common.stopLoading();
                            Toast.makeText(AddGroup.this, "Successfully added to " + groupName, Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, AddGroup.class));
                            finish();
                        }).addOnFailureListener(e -> {
                            common.stopLoading();
                            Toast.makeText(AddGroup.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                        });
                    }
                })
                .setNegativeButton("No", null)
                .show());

        parentContainer.addView(xmlView);
    }

    private void addToTask(String groupName) {
        DatabaseReference Data = FirebaseDatabase.getInstance().getReference();
        FirebaseApp.initializeApp(this);
        for (DataSnapshot id : common.snapshot.child("task").getChildren()) {
            long i = Long.parseLong(String.valueOf(id.getKey()));
            long x = Long.parseLong(common.getCDateTime());
            if (x < i) {
                if (id.child("groups/" + groupName).exists()) {
                    Data.child("server/" + common.server + "/task/" + id.getKey() + "/groups/" + groupName + "/" + common.rDBEmail).setValue("upcoming");
                }
            }
        }
    }

    private void addToMeet(String groupName) {
        DatabaseReference Data = FirebaseDatabase.getInstance().getReference();
        FirebaseApp.initializeApp(this);
        for (DataSnapshot id : common.snapshot.child("meet").getChildren()) {
            long i = Long.parseLong(String.valueOf(id.getKey()));
            long x = Long.parseLong(common.getCDateTime());
            if (x < i) {
                if (id.child("groups/" + groupName).exists()) {
                    Data.child("server/" + common.server + "/meet/" + id.getKey() + "/groups/" + groupName + "/" + common.rDBEmail).setValue("upcoming");
                }
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