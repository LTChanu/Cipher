package com.home.cipher;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class CreateGroup extends AppCompatActivity {

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

        common.startLoading(this,"Loading");
        CheckBox type = findViewById(R.id.group_type);
        loadName();

        type.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    groupType = "private";
                else
                    groupType = "public";
            }
        });
    }

    private void loadName() {
        parentContainer = findViewById(R.id.user_list);
        inflater = LayoutInflater.from(this);

        if (common.isNetworkConnected(this)) {
            DatabaseReference Data = FirebaseDatabase.getInstance().getReference();
            FirebaseApp.initializeApp(this);
            Data.child("user").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    parentContainer.removeAllViews();
                    int users = (int) snapshot.getChildrenCount();
                    list = new boolean[users];
                    mail = new String[users];

                    int i=0;
                    for (DataSnapshot snap : snapshot.getChildren()){
                        list[i]=false;
                        mail[i]= String.valueOf(snap.getKey());
                        loadOne(Objects.requireNonNull(snap.child("name").getValue()).toString(),i);
                        i++;
                    }
                    common.stopLoading();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void loadOne(String name, int i){
        LinearLayout xmlView = (LinearLayout) inflater.inflate(R.layout.one_user, parentContainer, false);
        CheckBox user = xmlView.findViewById(R.id.my_checkbox);
        int id= i;
        user.setText(name);

        user.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Perform actions based on checkbox state
            list[id]= isChecked;
        });

        parentContainer.addView(xmlView);
    }

    public void Add(View view) {
        boolean[] isRun = {true};
        common.startLoading(this,"Creating");
        TextInputLayout groupName = findViewById(R.id.string_group_name);
        String stringGroupName = Objects.requireNonNull(groupName.getEditText()).getText().toString();
        if (common.isNetworkConnected(this)) {
            DatabaseReference Data = FirebaseDatabase.getInstance().getReference();
            FirebaseApp.initializeApp(this);

            Data.child("group").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(isRun[0]){
                        isRun[0]=false;
                        if(snapshot.child(stringGroupName).exists()) {
                            common.stopLoading();
                            common.showMessage(CreateGroup.this, "Failed", "Group name is already use. Try different one.");
                        }
                        else
                            addGroup(stringGroupName);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void addGroup(String stringGroupName) {
        DatabaseReference Data = FirebaseDatabase.getInstance().getReference();
        FirebaseApp.initializeApp(this);
        for(int i=0; i<list.length;i++){
            if(list[i]){
                String name = String.valueOf(mail[i]);
                Data.child("group/" + stringGroupName + "/user/" + name).setValue(name);
            }
        }
        Data.child("group/" + stringGroupName + "/type").setValue(groupType).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                common.stopLoading();
                Toast.makeText(CreateGroup.this, "Group successfully created", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                common.stopLoading();
                Toast.makeText(CreateGroup.this, "Failed create", Toast.LENGTH_LONG).show();
            }
        });

    }
}