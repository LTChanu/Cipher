package com.home.cipher;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class ChangeName extends AppCompatActivity implements SharedVariable.DataSnapshotListener {

    private String dbEmail;
    private TextView name;
    private DatabaseReference Data;
    private StorageReference storageRef;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);
        common.startLoading(this, "Loading");

        dbEmail = common.rDBEmail;

        profile = findViewById(R.id.user_img);

        FirebaseApp.initializeApp(this);
        Data = FirebaseDatabase.getInstance().getReference();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        name = findViewById(R.id.name);
        showEmail();
        showName();
        loadImage(false);
    }

    private void loadImage(boolean updated) {
        if (common.snapshot.child("user/" + common.rDBEmail + "/img").exists() || updated) {
            profile.setImageBitmap(common.ImageBitmap.get(common.ImageID.indexOf(common.rDBEmail)));
        }
    }

    private void showEmail() {
        TextView email = findViewById(R.id.email);
        email.setText(dbEmail);
    }

    private void showName() {
        DataSnapshot snapshot = common.snapshot.child("user/" + dbEmail + "/name");
        String stringName = Objects.requireNonNull(snapshot.getValue()).toString();
        name.setText(stringName);
        common.stopLoading();
    }

    private void setName(String name) {
        if (common.isNetworkConnected(this)) {
            DatabaseReference Data = FirebaseDatabase.getInstance().getReference();
            FirebaseApp.initializeApp(this);
            Data.child("server/" + common.server + "/user/" + dbEmail + "/name").setValue(name).addOnSuccessListener(unused -> {
                Toast.makeText(ChangeName.this, "Name successfully changed", Toast.LENGTH_SHORT).show();
                showName();
            }).addOnFailureListener(e -> {
                common.stopLoading();
                Toast.makeText(ChangeName.this, "Error: " + e, Toast.LENGTH_LONG).show();
            });
        }
    }

    public void changeName(View view) {
        common.startLoading(this, "Updating");
        TextInputLayout name = findViewById(R.id.string_name);
        String stringName = Objects.requireNonNull(name.getEditText()).getText().toString();
        setName(stringName);
    }

    @Override
    public void onDataSnapshotChanged(String dataSnapshot) {
        loadImage(true);
    }

    public void changeProfilePhoto(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        common.startLoading(this, "Loading");
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            // Get the image URI
            Uri imageUri = data.getData();

            // Create a reference to the location where you want to store the image in Firebase Storage
            StorageReference imageRef = storageRef.child("images/" + common.server + "/" + common.rDBEmail + ".jpg");

            // Upload the image to Firebase Storage
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image upload successful
                        // You can retrieve the download URL of the uploaded image
                        imageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                            // Do something with the download URL (e.g., save it to Firebase Realtime Database)
                            //String imageUrl = downloadUrl.toString();
                            String link = "images/" + common.server + "/" + common.rDBEmail + ".jpg";
                            // Save the imageUrl to Firebase Realtime Database or perform any other operations
                            Data.child("server/" + common.server + "/user/" + common.rDBEmail + "/img").setValue(link);
                            common.stopLoading();
                            common.stopLoading();
                            common.stopLoading();
                            Toast.makeText(ChangeName.this, "Change Successful", Toast.LENGTH_SHORT).show();
                            loadImage(true);

                        }).addOnFailureListener(e -> {
                            common.stopLoading();
                            Toast.makeText(ChangeName.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                        });
                    })
                    .addOnFailureListener(e -> {
                        common.stopLoading();
                        Toast.makeText(ChangeName.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                    });
        } else
            common.stopLoading();
    }
}