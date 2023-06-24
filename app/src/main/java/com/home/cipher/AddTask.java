package com.home.cipher;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class AddTask extends AppCompatActivity {

    private LinearLayout parentContainer;
    private LayoutInflater inflater;
    private boolean[] list;
    private String[] mail;
    ArrayList<String> groupList, userList;
    private String key = "0";
    private TextInputEditText deadline, notify, name, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        common.startLoading(this, "Loading");

        deadline = findViewById(R.id.string_task_deadline);
        notify = findViewById(R.id.string_task_notify);
        name = findViewById(R.id.string_task_name);
        description = findViewById(R.id.string_task_description);


        loadName();
    }

    private void loadName() {
        parentContainer = findViewById(R.id.group_list);
        inflater = LayoutInflater.from(this);

        if (common.isNetworkConnected(this)) {
            DatabaseReference Data = FirebaseDatabase.getInstance().getReference();
            FirebaseApp.initializeApp(this);
            Data.child("group").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    parentContainer.removeAllViews();
                    int users = (int) snapshot.getChildrenCount();
                    list = new boolean[users];
                    mail = new String[users];

                    int i = 0;
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        if(snap.child("user/"+common.rDBEmail).exists()) {
                            list[i] = false;
                            String em = String.valueOf(snap.getKey());
                            mail[i] = em;
                            loadOne(em, i);
                            i++;
                        }
                    }
                    common.stopLoading();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
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

    public void selectDeadline(View view) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create a DatePickerDialog to pick the date
        DatePickerDialog datePickerDialog = new DatePickerDialog(AddTask.this,
                (view1, selectedYear, selectedMonth, selectedDay) -> {
                    // The selected date is returned in the parameters
                    // Handle the selected date
                    //String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                    String keyDate = String.format(Locale.getDefault(), "%04d%02d%02d", selectedYear, selectedMonth + 1, selectedDay);

                    // Create a TimePickerDialog to pick the time
                    TimePickerDialog timePickerDialog = new TimePickerDialog(AddTask.this,
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
        DatePickerDialog datePickerDialog = new DatePickerDialog(AddTask.this,
                (view1, selectedYear, selectedMonth, selectedDay) -> {
                    // The selected date is returned in the parameters
                    // Handle the selected date
                    //String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                    String keyDate = String.format(Locale.getDefault(), "%04d%02d%02d", selectedYear, selectedMonth + 1, selectedDay);

                    // Create a TimePickerDialog to pick the time
                    TimePickerDialog timePickerDialog = new TimePickerDialog(AddTask.this,
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

    public void AddCiph(View view) throws ParseException {
        String stringDeadline, stringNotify, stringName, stringDescription;

        stringName = Objects.requireNonNull(name.getText()).toString();
        stringDescription = Objects.requireNonNull(description.getText()).toString();
        stringDeadline = Objects.requireNonNull(deadline.getText()).toString();
        stringNotify = Objects.requireNonNull(notify.getText()).toString();

        if(stringName.equals("") || stringDeadline.equals("") || stringNotify.equals("")){
            Toast.makeText(this, "Fill All details.", Toast.LENGTH_LONG).show();
        }else {
            boolean[] isRun = {true};
            getGroups();
            getUsers();
            common.startLoading(this,"Creating ID");
            userList = common.removeDuplicate(userList);
            String dateID = common.convertToID(stringDeadline);
            if (common.isNetworkConnected(this)) {
                DatabaseReference Data = FirebaseDatabase.getInstance().getReference();
                FirebaseApp.initializeApp(this);
                Data.child("task").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(isRun[0]){
                            isRun[0] =false;
                            String ID;
                            int i =10;
                            boolean x;
                            do{
                                ID=dateID+i;
                                i++;
                                x = snapshot.child(ID).exists();
                            }while (x);
                            common.stopLoading();
                            addTask(ID,stringName,stringDescription,stringNotify);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }

    private void addTask(String id, String stringName, String stringDescription, String stringNotify) {
        if (common.isNetworkConnected(this)) {
            common.startLoading(this, "Adding");
            DatabaseReference Data = FirebaseDatabase.getInstance().getReference();
            FirebaseApp.initializeApp(this);

            Data.child("task/" + id + "/name").setValue(stringName);
            if(stringDescription!=null)
                Data.child("task/" + id + "/description").setValue(stringDescription);
            for(String user : userList){
                Data.child("task/" + id + "/user/"+user).setValue("upcoming");
            }
            Data.child("task/" + id + "/notify").setValue(stringNotify).addOnSuccessListener(unused -> {
                common.stopLoading();
                Toast.makeText(this, "Ciph added.", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                common.stopLoading();
                Toast.makeText(AddTask.this, "Error: "+e, Toast.LENGTH_LONG).show();
            });
        }
    }

    private void getUsers() {
        boolean[] isRun = {true};
        userList = new ArrayList<>();
        if (common.isNetworkConnected(this)) {
            DatabaseReference Data = FirebaseDatabase.getInstance().getReference();
            FirebaseApp.initializeApp(this);
            Data.child("group").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(isRun[0]) {
                        isRun[0] = false;
                        for (String group : groupList) {
                            for (DataSnapshot snap : snapshot.child(group+"/user").getChildren()) {
                                userList.add(String.valueOf(snap.getKey()));
                            }
                        }
                        common.stopLoading();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void getGroups() {
        groupList = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            if (list[i])
                groupList.add(String.valueOf(mail[i]));
        }
    }
}