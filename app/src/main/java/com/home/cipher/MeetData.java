package com.home.cipher;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MeetData extends AppCompatActivity implements SharedVariable.DataSnapshotListener {

    private String link;
    private ArrayList<String> groupList;
    private LinearLayout parentContainer;
    private LayoutInflater inflater;
    private TextView name, discription;
    private String dt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meet_data);
        common.startLoading(this, "Loading");

        parentContainer = findViewById(R.id.comment_list);
        inflater = LayoutInflater.from(this);

        checkEdit();
        loadData();
        loadComment();
    }

    private void checkEdit() {
        if(!common.isEditable(String.valueOf(common.snapshot.child("/meet/" + common.dataID + "/create").getValue()))){
            LinearLayout parentLayout = findViewById(R.id.parent_layout);
            ImageButton button = findViewById(R.id.edit_button);
            parentLayout.removeView(button);
        }
    }

    private void loadComment() {
        parentContainer.removeAllViews();
        DataSnapshot dataSnapshot = common.snapshot.child("meet/"+common.dataID+"/comment");
        for (DataSnapshot comment : dataSnapshot.getChildren()){
            commentAdd(Objects.requireNonNull(comment.child("mail").getValue()).toString(),
                    Objects.requireNonNull(comment.child("time").getValue()).toString(),
                    Objects.requireNonNull(comment.child("description").getValue()).toString());
        }
    }

    private void commentAdd(String mail, String time, String description){
        // Inflate the XML layout to obtain the view object
        LinearLayout xmlView = (LinearLayout) inflater.inflate(R.layout.one_comment, parentContainer, false);

        // Customize the view or set data to it
        TextView userName=xmlView.findViewById(R.id.comment_name);
        TextView date=xmlView.findViewById(R.id.comment_date_time);
        TextView comment=xmlView.findViewById(R.id.comment_body);
        CircleImageView img =xmlView.findViewById(R.id.user_img);

        date.setText(time);
        comment.setText(description);

        DataSnapshot user = common.snapshot.child("user/"+mail);

        userName.setText(String.valueOf(user.child("name").getValue()));

        if(user.child("img").exists()){
            Handler handler = new Handler(Looper.getMainLooper());

            try {
                img.setImageBitmap(common.ImageBitmap.get(common.ImageID.indexOf(mail)));
            }catch (Exception e){
                Runnable delayRunnable = this::loadComment;
                handler.postDelayed(delayRunnable, 5000);
            }
        }

        // Add the view to the parent container
        parentContainer.addView(xmlView);
    }

    private void loadData() {
        TextView  date, time, notifyDate, notifyTime;
        name = findViewById(R.id.Name);
        discription = findViewById(R.id.Description);
        date = findViewById(R.id.Date);
        time = findViewById(R.id.Time);
        notifyDate = findViewById(R.id.Notify_date);
        notifyTime = findViewById(R.id.Notify_time);

        DataSnapshot snapshot = common.snapshot.child("meet/" + common.dataID);
        name.setText(String.valueOf(snapshot.child("name").getValue()));
        discription.setText(String.valueOf(snapshot.child("description").getValue()));

        link = String.valueOf(snapshot.child("link").getValue());
        String[] dayTime;
        try {
            dayTime = common.convertToDayTime(common.dataID);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        date.setText(dayTime[0]);
        time.setText(dayTime[1]);

        dt = String.valueOf(snapshot.child("notify/1").getValue());
        try {
            notifyDate.setText(common.convertToDate(dt));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        try {
            notifyTime.setText(common.convertToTime(dt));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        common.stopLoading();
    }

    @SuppressLint("QueryPermissionsNeeded")
    public void goToMeeting(View view) {
        Uri webpage = Uri.parse(link);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Check if the YouTube app is installed
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Open the YouTube link in the app
            startActivity(intent);
        } else {
            // If the YouTube app is not installed, open the link in a web browser
            intent.setPackage(null);
            startActivity(intent);
        }
    }

    public void done(View view) {
        new AlertDialog.Builder(this).setTitle("Done Meeting").setMessage("Are you sure this Meeting is complete?").setPositiveButton("Yes", (dialog, which) -> {
            try {
                finishTask();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }).setNegativeButton("No", null).show();
    }

    private void finishTask() throws ParseException {
        if (common.isNetworkConnected(this)) {
            FirebaseApp.initializeApp(this);
            DatabaseReference Data = FirebaseDatabase.getInstance().getReference();
            getGroups();
            for (String group : groupList) {
                Data.child("server/" + common.server + "/meet/" + common.dataID + "/groups/" + group + "/" + common.rDBEmail).setValue("done");
            }
            startActivity(new Intent(this, Home.class));
            finish();
        }
    }

    private void getGroups() {
        groupList = new ArrayList<>();
        for (DataSnapshot group : common.snapshot.child("meet/" + common.dataID + "/groups/").getChildren()) {
            if (group.child(common.rDBEmail).exists())
                groupList.add(String.valueOf(group.getKey()));
        }
    }

    public void copyLink(View view) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", link);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Link copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDataSnapshotChanged(String dataSnapshot) {
        loadData();
        loadComment();
    }

    public void AddMeetComment(View view) {
        //popup
        PopupGetString popup = new PopupGetString(getLayoutInflater(), getWindow());
        popup.showAndWaitForInput("New Comment", "Enter your comment", "Type here");
        popup.ok.setOnClickListener(v -> {
            //check OTP
            if (!popup.text.getText().toString().isEmpty()) {
                String CID = common.getCDateTime();
                String dateTime = common.getCDateTimeString();

                DatabaseReference Data = FirebaseDatabase.getInstance().getReference();
                FirebaseApp.initializeApp(this);

                Data.child("server/" + common.server + "/meet/" + common.dataID + "/comment/" + CID + "/time").setValue(dateTime);
                Data.child("server/" + common.server + "/meet/" + common.dataID + "/comment/" + CID + "/mail").setValue(common.rDBEmail);
                Data.child("server/" + common.server + "/meet/" + common.dataID + "/comment/" + CID + "/description").setValue(popup.text.getText().toString())
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(MeetData.this, "Comment added", Toast.LENGTH_LONG).show();
                            popup.popupWindow.dismiss();
                            loadComment();
                        }).addOnFailureListener(e -> Toast.makeText(MeetData.this, "Error: " + e, Toast.LENGTH_LONG).show());
            } else
                Toast.makeText(this, "Please Enter Your Comment", Toast.LENGTH_LONG).show();
        });
        popup.cansel.setOnClickListener(v -> popup.popupWindow.dismiss());
    }

    public void RefreshMeetComment(View view) {
        loadComment();
    }

    public void editMeet(View view) {
        common.edit = true;
        common.taskName = name.getText().toString();
        common.taskDescription = discription.getText().toString();
        common.startTime = common.convertToDateTimeFormat(common.dataID);
        common.notifyTime = dt;
        common.link = link;
        startActivity(new Intent(this, AddMeet.class));
    }
}