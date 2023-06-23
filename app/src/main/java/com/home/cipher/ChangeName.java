package com.home.cipher;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ChangeName extends AppCompatActivity {

    private String dbEmail;
    private TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);
        common.startLoading(this, "Loading");

        dbEmail = common.rDBEmail;

        name = findViewById(R.id.name);
        showEmail();
        showName();
    }

    private void showEmail(){
        TextView email = findViewById(R.id.email);
        email.setText(dbEmail);
    }

    private void showName() {
        if (common.isNetworkConnected(this)) {
            DatabaseReference Data = FirebaseDatabase.getInstance().getReference();
            FirebaseApp.initializeApp(this);
            Data.child("user/" + dbEmail + "/name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String stringName = Objects.requireNonNull(snapshot.getValue()).toString();
                    name.setText(stringName);
                    common.stopLoading();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void setName(String name) {
        if (common.isNetworkConnected(this)) {
            DatabaseReference Data = FirebaseDatabase.getInstance().getReference();
            FirebaseApp.initializeApp(this);
            Data.child("user/" + dbEmail + "/name").setValue(name).addOnSuccessListener(unused -> {
                Toast.makeText(ChangeName.this, "Name successfully changed", Toast.LENGTH_SHORT).show();
                showName();
            }).addOnFailureListener(e ->{
                    common.stopLoading();
                    Toast.makeText(ChangeName.this, "Error: " + e, Toast.LENGTH_LONG).show();
            });
        }
    }

    public void changeName(View view) {
        common.startLoading(this,"Updating");
        TextInputLayout name = findViewById(R.id.string_name);
        String stringName = Objects.requireNonNull(name.getEditText()).getText().toString();
        setName(stringName);
    }
}