package com.home.cipher;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateGroup extends AppCompatActivity implements SharedVariable.DataSnapshotListener {

    private LinearLayout parentContainer;
    private LayoutInflater inflater;
    private String groupType = "public";

    boolean[] list;
    String[] mail;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        common.startLoading(this, "Loading");

        FirebaseApp.initializeApp(this);

        CheckBox type = findViewById(R.id.group_type);
        loadName();

        type.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                groupType = "private";
            else
                groupType = "public";
        });
    }

    private void loadName() {
        parentContainer = findViewById(R.id.user_list);
        inflater = LayoutInflater.from(this);

        DataSnapshot snapshot = common.snapshot.child("user");
        parentContainer.removeAllViews();
        int users = (int) snapshot.getChildrenCount();
        list = new boolean[users];
        mail = new String[users];

        int i = 0;
        for (DataSnapshot snap : snapshot.getChildren()) {
            list[i] = false;
            mail[i] = String.valueOf(snap.getKey());
            if (snap.child("img").exists())
                loadOne(Objects.requireNonNull(snap.child("name").getValue()).toString(), i, Objects.requireNonNull(snap.getKey()));
            else
                loadOne(Objects.requireNonNull(snap.child("name").getValue()).toString(), i);
            i++;
        }
        common.stopLoading();
    }

    private void loadOne(String name, int i, String imageID) {
        LinearLayout xmlView = (LinearLayout) inflater.inflate(R.layout.one_user, parentContainer, false);
        CheckBox user = xmlView.findViewById(R.id.my_checkbox);
        CircleImageView img = xmlView.findViewById(R.id.user_img);
        int id = i;
        user.setText(name);

        Handler handler = new Handler(Looper.getMainLooper());
        try {
            img.setImageBitmap(common.ImageBitmap.get(common.ImageID.indexOf(imageID)));
        }catch (Exception e){
            Runnable delayRunnable = this::loadName;
            handler.postDelayed(delayRunnable, 5000);
        }

        user.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Perform actions based on checkbox state
            list[id] = isChecked;
        });

        parentContainer.addView(xmlView);
    }

    private void loadOne(String name, int i) {
        LinearLayout xmlView = (LinearLayout) inflater.inflate(R.layout.one_user, parentContainer, false);
        CheckBox user = xmlView.findViewById(R.id.my_checkbox);
        int id = i;
        user.setText(name);

        user.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Perform actions based on checkbox state
            list[id] = isChecked;
        });

        parentContainer.addView(xmlView);
    }

    public void Add(View view) {
        common.startLoading(this, "Creating");
        TextInputLayout groupName = findViewById(R.id.string_group_name);
        String stringGroupName = Objects.requireNonNull(groupName.getEditText()).getText().toString();
        Button sendBtn = findViewById(R.id.logBtn);
        DataSnapshot snapshot = common.snapshot.child("group");
        sendBtn.setEnabled(false);
        if (snapshot.child(stringGroupName).exists()) {
            common.stopLoading();
            common.showMessage(CreateGroup.this, "Failed", "Group name is already use. Try different one.");
        } else
            addGroup(stringGroupName);

        new CountDownTimer(10000, 1000) { // 60000 milliseconds = 1 minutes
            public void onTick(long millisUntilFinished) {
                // do nothing
            }

            public void onFinish() {
                sendBtn.setEnabled(true); // enable the button after 5 minutes
            }
        }.start();
    }

    private void addGroup(String stringGroupName) {
        DatabaseReference Data = FirebaseDatabase.getInstance().getReference();
        FirebaseApp.initializeApp(this);
        for (int i = 0; i < list.length; i++) {
            if (list[i]) {
                String name = String.valueOf(mail[i]);
                Data.child("server/" + common.server + "/group/" + stringGroupName + "/user/" + name).setValue(name);
            }
        }
        Data.child("server/" + common.server + "/group/" + stringGroupName + "/type").setValue(groupType).addOnSuccessListener(unused -> {
            common.stopLoading();
            Toast.makeText(CreateGroup.this, "Group successfully created", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            common.stopLoading();
            Toast.makeText(CreateGroup.this, "Failed create", Toast.LENGTH_LONG).show();
        });

    }

    public void onBackPressed() {
        startActivity(new Intent(this, Home.class));
        finish();
    }

    @Override
    public void onDataSnapshotChanged(String dataSnapshot) {
        loadName();
    }
}