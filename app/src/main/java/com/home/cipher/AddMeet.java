package com.home.cipher;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class AddMeet extends AppCompatActivity implements SharedVariable.DataSnapshotListener {

    private LinearLayout parentContainer;
    private LayoutInflater inflater;
    private boolean[] list;
    private String[] mail;
    private ArrayList<String> groupList, userList;
    private String key = "0";
    private TextInputEditText deadline, notify, name, description, link;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meet);

        common.startLoading(this, "Loading");

        deadline = findViewById(R.id.string_meet_start_time);
        notify = findViewById(R.id.string_meet_notify);
        name = findViewById(R.id.string_meet_name);
        description = findViewById(R.id.string_meet_description);
        link = findViewById(R.id.string_meet_link);

        if (common.edit)
            loadData();
        loadName();
    }

    private void loadData() {
        deadline.setText(common.startTime);
        notify.setText(common.notifyTime);
        name.setText(common.taskName);
        description.setText(common.taskDescription);
        link.setText(common.link);
    }

    private void loadName() {
        parentContainer = findViewById(R.id.group_list);
        inflater = LayoutInflater.from(this);

        DataSnapshot snapshot = common.snapshot.child("group");
        parentContainer.removeAllViews();
        int users = (int) snapshot.getChildrenCount();
        list = new boolean[users];
        mail = new String[users];

        int i = 0;
        for (DataSnapshot snap : snapshot.getChildren()) {
            if (snap.child("user/" + common.rDBEmail).exists()) {
                list[i] = false;
                String em = String.valueOf(snap.getKey());
                mail[i] = em;
                loadOne(em, i);
                i++;
            }
        }
        common.stopLoading();
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

    public void selectStartTime(View view) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create a DatePickerDialog to pick the date
        DatePickerDialog datePickerDialog = new DatePickerDialog(AddMeet.this,
                (view1, selectedYear, selectedMonth, selectedDay) -> {
                    // The selected date is returned in the parameters
                    // Handle the selected date
                    //String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                    String keyDate = String.format(Locale.getDefault(), "%04d%02d%02d", selectedYear, selectedMonth + 1, selectedDay);

                    // Create a TimePickerDialog to pick the time
                    TimePickerDialog timePickerDialog = new TimePickerDialog(AddMeet.this,
                            (view2, selectedHour, selectedMinute) -> {
                                // The selected time is returned in the parameters
                                // Handle the selected time
                                //String selectedTime = selectedHour + ":" + selectedMinute;
                                String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                                String keyTime = String.format(Locale.getDefault(), "%02d%02d", selectedHour, selectedMinute);

                                // Combine the selected date and time
                                String selectedDateTime = selectedDate + " " + selectedTime;
                                key = keyDate + keyTime;

                                // Set the selected date and time in the EditText
                                deadline.setText(selectedDateTime);
                            }, hour, minute, true);

                    // Show the TimePickerDialog
                    timePickerDialog.show();
                }, year, month, day);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }

    public void selectNotify(View view) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create a DatePickerDialog to pick the date
        DatePickerDialog datePickerDialog = new DatePickerDialog(AddMeet.this,
                (view1, selectedYear, selectedMonth, selectedDay) -> {
                    // The selected date is returned in the parameters
                    // Handle the selected date
                    //String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                    String keyDate = String.format(Locale.getDefault(), "%04d%02d%02d", selectedYear, selectedMonth + 1, selectedDay);

                    // Create a TimePickerDialog to pick the time
                    TimePickerDialog timePickerDialog = new TimePickerDialog(AddMeet.this,
                            (view2, selectedHour, selectedMinute) -> {
                                // The selected time is returned in the parameters
                                // Handle the selected time
                                //String selectedTime = selectedHour + ":" + selectedMinute;
                                String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                                String keyTime = String.format(Locale.getDefault(), "%02d%02d", selectedHour, selectedMinute);

                                // Combine the selected date and time
                                String selectedDateTime = selectedDate + " " + selectedTime;
                                key = keyDate + keyTime;

                                // Set the selected date and time in the EditText
                                notify.setText(selectedDateTime);
                            }, hour, minute, true);

                    // Show the TimePickerDialog
                    timePickerDialog.show();
                }, year, month, day);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }

    @SuppressLint("NotConstructor")
    public void AddMeeting(View view) {
        String stringDeadline, stringNotify, stringName, stringDescription, stringLink;

        stringName = Objects.requireNonNull(name.getText()).toString();
        stringDescription = Objects.requireNonNull(description.getText()).toString();
        stringDeadline = Objects.requireNonNull(deadline.getText()).toString();
        stringNotify = Objects.requireNonNull(notify.getText()).toString();
        stringLink = Objects.requireNonNull(link.getText()).toString();

        if (stringName.equals("") || stringDeadline.equals("") || stringNotify.equals("") || stringLink.equals("")) {
            Toast.makeText(this, "Fill All details.", Toast.LENGTH_LONG).show();
        } else {
            Button sendBtn = findViewById(R.id.logBtn);
            sendBtn.setEnabled(false);
            getGroups();
            common.startLoading(this, "Creating ID");
            String dateID = common.convertToID(stringDeadline);
            DataSnapshot snapshot = common.snapshot.child("meet");
            String ID;
            int i = 10;
            boolean x;
            do {
                ID = dateID + i;
                i++;
                x = snapshot.child(ID).exists();
            } while (x);
            common.stopLoading();
            addMeet(ID, stringName, stringDescription, stringNotify, stringLink);
            new CountDownTimer(10000, 1000) { // 60000 milliseconds = 1 minutes
                public void onTick(long millisUntilFinished) {
                    // do nothing
                }

                public void onFinish() {
                    sendBtn.setEnabled(true); // enable the button after 5 minutes
                }
            }.start();
        }
    }

    private void addMeet(String id, String stringName, String stringDescription, String stringNotify, String stringLink) {
        if (common.isNetworkConnected(this)) {
            common.startLoading(this, "Adding");
            DatabaseReference Data = FirebaseDatabase.getInstance().getReference();
            FirebaseApp.initializeApp(this);

            Data.child("server/" + common.server + "/meet/" + id + "/name").setValue(stringName);
            Data.child("server/" + common.server + "/meet/" + id + "/link").setValue(stringLink);
            if (stringDescription != null)
                Data.child("server/" + common.server + "/meet/" + id + "/description").setValue(stringDescription);
            for (String group : groupList) {
                Data.child("server/" + common.server + "/meet/" + id + "/groups/" + group + "/user").setValue("upcoming");
                getUsers();
                for (String user : userList) {
                    Data.child("server/" + common.server + "/meet/" + id + "/groups/" + group + "/" + user).setValue("upcoming");
                }
            }
            Data.child("server/" + common.server + "/meet/" + id + "/create").setValue(common.getCDateTime());
            Data.child("server/" + common.server + "/meet/" + id + "/notify/1").setValue(stringNotify).addOnSuccessListener(unused -> {
                if (common.edit) {
                    Data.child("server/" + common.server + "/meet/" + common.dataID).removeValue();
                    common.dataID = id;
                    common.stopLoading();
                    Toast.makeText(this, "Meeting Updated.", Toast.LENGTH_SHORT).show();
                } else {
                    common.stopLoading();
                    Toast.makeText(this, "Meeting added.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(e -> {
                common.stopLoading();
                Toast.makeText(AddMeet.this, "Error: " + e, Toast.LENGTH_LONG).show();
            });
        }
    }

    private void getUsers() {
        userList = new ArrayList<>();
        DataSnapshot snapshot = common.snapshot.child("group");
        for (String group : groupList) {
            for (DataSnapshot snap : snapshot.child(group + "/user").getChildren()) {
                userList.add(String.valueOf(snap.getKey()));
            }
        }
        common.stopLoading();
    }

    private void getGroups() {
        groupList = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            if (list[i])
                groupList.add(String.valueOf(mail[i]));
        }
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