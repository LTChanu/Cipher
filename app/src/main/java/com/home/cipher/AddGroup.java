package com.home.cipher;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
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

public class AddGroup extends AppCompatActivity {

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

        if (common.isNetworkConnected(this)) {
            DatabaseReference Data = FirebaseDatabase.getInstance().getReference();
            FirebaseApp.initializeApp(this);
            Data.child("group").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    parentContainer.removeAllViews();
                    for (DataSnapshot snap : snapshot.getChildren()){
                        if(!snap.child("user/"+common.rDBEmail).exists() && String.valueOf(snap.child("type").getValue()).equals("public"))
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
        group.setOnClickListener(v -> new AlertDialog.Builder(AddGroup.this)
                .setTitle("Confirm")
                .setMessage("Do you sure you want add to " + groupName + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    common.startLoading(AddGroup.this, "Adding");
                    if (common.isNetworkConnected(AddGroup.this)) {
                        DatabaseReference Data = FirebaseDatabase.getInstance().getReference();
                        FirebaseApp.initializeApp(AddGroup.this);
                        Data.child("group/" + groupName + "/user/" + name).setValue(name).addOnSuccessListener(unused -> {
                            common.stopLoading();
                            Toast.makeText(AddGroup.this, "Successfully added to "+groupName, Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            common.stopLoading();
                            Toast.makeText(AddGroup.this, "Error: "+e, Toast.LENGTH_SHORT).show();
                        });
                    }
                })
                .setNegativeButton("No", null)
                .show());

        parentContainer.addView(xmlView);
    }
}