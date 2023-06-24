package com.home.cipher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LeaveGroup extends AppCompatActivity {

    private LinearLayout parentContainer;
    private LayoutInflater inflater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_group);

        common.startLoading(this, "Loading");
        loadGroups();
    }

    private void loadGroups() {
        parentContainer = findViewById(R.id.group_list);
        inflater = LayoutInflater.from(this);

        if (common.isNetworkConnected(this)) {
            DatabaseReference Data = FirebaseDatabase.getInstance().getReference();
            FirebaseApp.initializeApp(this);
            Data.child("group").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    parentContainer.removeAllViews();
                    for (DataSnapshot snap : snapshot.getChildren()){
                        if(snap.child("user/"+common.rDBEmail).exists())
                            loadGroup(String.valueOf(snap.getKey()));
                    }
                    common.stopLoading();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
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
                        Data.child("group/" + groupName + "/user/" + name).removeValue().addOnSuccessListener(unused -> {
                            common.stopLoading();
                            Toast.makeText(LeaveGroup.this, "Successfully leaved from "+groupName, Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            common.stopLoading();
                            Toast.makeText(LeaveGroup.this, "Error: "+e, Toast.LENGTH_SHORT).show();
                        });
                    }
                })
                .setNegativeButton("No", null)
                .show());

        parentContainer.addView(xmlView);
    }
}