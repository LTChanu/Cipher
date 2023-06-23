package com.home.cipher;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
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

public class CreateGroup extends AppCompatActivity {

    private LinearLayout parentContainer;
    private LayoutInflater inflater;

    boolean[] list;
    String[] mail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        common.startLoading(this,"Loading");
        loadName();
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
        common.startLoading(this,"Creating");
        TextInputLayout groupName = findViewById(R.id.string_group_name);
        String stringGroupName = Objects.requireNonNull(groupName.getEditText()).getText().toString();
        if (common.isNetworkConnected(this)) {
            DatabaseReference Data = FirebaseDatabase.getInstance().getReference();
            FirebaseApp.initializeApp(this);

            for(int i=0; i<list.length;i++){
                if(list[i]){
                    String name = String.valueOf(mail[i]);
                    Data.child("group/" + stringGroupName + "/" + name).setValue(name);
                }
            }
           common.stopLoading();
            Toast.makeText(this, "Group successfully created", Toast.LENGTH_SHORT).show();
        }
    }
}