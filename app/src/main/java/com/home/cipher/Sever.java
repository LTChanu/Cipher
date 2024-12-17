package com.home.cipher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Sever extends AppCompatActivity {

    private TextInputEditText name, password;
    private SharedVariable sharedVariable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sever);

        name = findViewById(R.id.string_server_name);
        password = findViewById(R.id.string_server_password);

        sharedVariable = new SharedVariable(this);
        load();
    }

    private void load() {
        TextView sever = findViewById(R.id.name);
        sever.setText(sharedVariable.getServer());
    }

    public void Join(View view) {
        if (!String.valueOf(name.getText()).trim().isEmpty() && !String.valueOf(password.getText()).trim().isEmpty()) {
            joinToServer();
        } else {
            Toast.makeText(this, "Please provide details", Toast.LENGTH_SHORT).show();
        }
    }

    private void joinToServer() {
        boolean[] isRun = {true};
        common.startLoading(this, "Loading");
        String stringName = Objects.requireNonNull(name.getText()).toString();
        String stringPassword = Objects.requireNonNull(password.getText()).toString();

        FirebaseApp.initializeApp(this);
        DatabaseReference Data = FirebaseDatabase.getInstance().getReference();

        Data.child("server").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isRun[0]) {
                    isRun[0] = false;
                    if (snapshot.child(stringName).exists()) {
                        if (String.valueOf(snapshot.child(stringName + "/password").getValue()).equals(stringPassword)) {
                            addData(stringName, snapshot);
                        } else {
                            common.stopLoading();
                            Toast.makeText(Sever.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        common.stopLoading();
                        Toast.makeText(Sever.this, "Server Not Found", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                common.stopLoading();
                Toast.makeText(Sever.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addData(String stringName, DataSnapshot sever) {
        boolean[] isRun = {true};
        FirebaseApp.initializeApp(this);
        DatabaseReference Data = FirebaseDatabase.getInstance().getReference();

        Data.child("user").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isRun[0]) {
                    isRun[0] = false;
                    for (DataSnapshot user : snapshot.getChildren()) {
                        if (String.valueOf(user.getKey()).equals(sharedVariable.getDBEmail())) {
                            String userEmail, userName;
                            userEmail = String.valueOf(user.child("email").getValue());
                            userName = String.valueOf(user.child("name").getValue());

                            Data.child("server/" + stringName + "/user/" + sharedVariable.getDBEmail() + "/email").setValue(userEmail);
                            Data.child("server/" + stringName + "/user/" + sharedVariable.getDBEmail() + "/name").setValue(userName).addOnSuccessListener(unused -> {
                                sharedVariable.setServer(stringName);
                                common.snapshot = sever.child(stringName);
                                common.server = stringName;
                                sharedVariable.setDataSnapshot(sever.child(stringName));
                                load();
                                common.stopLoading();
                                Toast.makeText(Sever.this, "Joined to Sever", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(e -> {
                                common.stopLoading();
                                Toast.makeText(Sever.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                            });
                            return;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                common.stopLoading();
                Toast.makeText(Sever.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void create(View view) {
        if (!String.valueOf(name.getText()).trim().isEmpty() && !String.valueOf(password.getText()).trim().isEmpty()) {
            CreateServer();
        } else {
            Toast.makeText(this, "Please provide details", Toast.LENGTH_SHORT).show();
        }
    }

    private void CreateServer() {
        boolean[] isRun = {true};
        common.startLoading(this, "Loading");
        String stringName = Objects.requireNonNull(name.getText()).toString();
        String stringPassword = Objects.requireNonNull(password.getText()).toString();

        FirebaseApp.initializeApp(this);
        DatabaseReference Data = FirebaseDatabase.getInstance().getReference();

        Data.child("server").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isRun[0]) {
                    isRun[0] = false;
                    if (!snapshot.child(stringName).exists()) {
                        Data.child("server/" + stringName + "/editableTime").setValue(30);
                        Data.child("server/" + stringName + "/password").setValue(stringPassword).addOnSuccessListener(unused -> {
                            Toast.makeText(Sever.this, "Server Successfully Created", Toast.LENGTH_SHORT).show();
                            addData(stringName, snapshot);
                        }).addOnFailureListener(e -> Toast.makeText(Sever.this, "Server NOT Create", Toast.LENGTH_LONG).show());
                    } else {
                        common.stopLoading();
                        Toast.makeText(Sever.this, "Server Name Already exists", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                common.stopLoading();
                Toast.makeText(Sever.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onBackPressed() {
        if (!sharedVariable.getServer().equals("null")) {
            startActivity(new Intent(this, Home.class));
            finish();
        } else
            Toast.makeText(this, "Please Add/Create Server", Toast.LENGTH_SHORT).show();
    }
}