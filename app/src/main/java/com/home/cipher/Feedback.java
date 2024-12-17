package com.home.cipher;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Feedback extends AppCompatActivity {
    private EditText body;
    private ImageView img;
    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        body = findViewById(R.id.feedback_body);
        img = findViewById(R.id.feedback_img);
    }

    public void GetImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    public void Submit(View view) {
        if (!body.getText().toString().trim().equals("")) {
            SubmitImg(imageUri != null);
        } else
            Toast.makeText(this, "Please Enter Feedback.", Toast.LENGTH_LONG).show();
    }

    private void SubmitImg(boolean image) {
        String now = common.getCDateTime();
        FirebaseApp.initializeApp(this);
        DatabaseReference Data = FirebaseDatabase.getInstance().getReference();
        if (image) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference imageRef = storageRef.child("feedback/" + common.server + "/" + common.rDBEmail + "/" + now + ".jpg");

            // Upload the image to Firebase Storage
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image upload successful
                        // You can retrieve the download URL of the uploaded image
                        imageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                            // Do something with the download URL (e.g., save it to Firebase Realtime Database)
                            //String imageUrl = downloadUrl.toString();
                            String link = "feedback/" + common.server + "/" + common.rDBEmail + "/" + now + ".jpg";
                            // Save the imageUrl to Firebase Realtime Database or perform any other operations
                            Data.child("feedback/" + common.server + "/" + common.rDBEmail + "/" + now + "/img").setValue(link);
                            common.stopLoading();

                        }).addOnFailureListener(e -> {
                            common.stopLoading();
                            Toast.makeText(Feedback.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                        });
                    })
                    .addOnFailureListener(e -> {
                        common.stopLoading();
                        Toast.makeText(Feedback.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                    });
        }

        Data.child("feedback/" + common.server + "/" + common.rDBEmail + "/" + now + "/details").setValue(body.getText().toString())
                .addOnSuccessListener(unused -> {
                    Toast.makeText(Feedback.this, "Feedback Successfully submitted", Toast.LENGTH_SHORT).show();
                    body.setText("");
                    img.setImageResource(0);
                }).addOnFailureListener(e -> Toast.makeText(Feedback.this, "Error: " + e, Toast.LENGTH_SHORT).show());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        common.startLoading(this, "Loading");
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            // Get the image URI
            imageUri = data.getData();
            img.setImageURI(imageUri);
            common.stopLoading();
        } else
            common.stopLoading();
    }

    public void onBackPressed() {
        startActivity(new Intent(this, Home.class));
        finish();
    }
}