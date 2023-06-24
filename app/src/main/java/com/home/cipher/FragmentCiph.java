package com.home.cipher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Objects;

public class FragmentCiph extends Fragment {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    //My attribute
    private LinearLayout parentLayout;
    private static LayoutInflater inflater;
    private final ArrayList<String> dataKey = new ArrayList<>();

    public FragmentCiph() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ciph, container, false);

        common.startLoading(context,"Loading");
        parentLayout = view.findViewById(R.id.ciph_view);
        FragmentCiph.inflater = inflater;

        loadData();

        return view;
    }

    private void loadData() {
        ArrayList<String> dataName = new ArrayList<>();

        FirebaseApp.initializeApp(context);
        DatabaseReference Data = FirebaseDatabase.getInstance().getReference();

        Data.child("task").addValueEventListener(new ValueEventListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataKey.clear();
                dataName.clear();
                clearViewChild();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Get the value of each child in the node
                    if(String.valueOf(snapshot.child("user/"+common.rDBEmail).getValue()).equals("upcoming")) {
                        dataName.add(Objects.requireNonNull(snapshot.child("name").getValue()).toString());
                        dataKey.add(snapshot.getKey());
                    }
                }
                for (int i = 0; i < dataKey.size(); i++) {
                    try {
                        addData(dataKey.get(i),dataName.get(i), i);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
                common.stopLoading();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addData(String key, String name, int id) throws ParseException {
        // Inflate the custom layout XML file for the clickable element
        View clickableElement = inflater.inflate(R.layout.one_task, parentLayout, false);

        // Find the image view and text view in the clickable element
        TextView nameTextView = clickableElement.findViewById(R.id.task_name);
        TextView dayTextView = clickableElement.findViewById(R.id.task_date);
        TextView timeTextView = clickableElement.findViewById(R.id.task_time);
        String[] dayTime = common.convertToDayTime(key);
        LinearLayout clickMe = clickableElement.findViewById(R.id.click_me);
        // Set the image resource ID and person's name for the clickable element
        nameTextView.setText(name);
        nameTextView.setId(id);
        dayTextView.setText(dayTime[0]);
        timeTextView.setText(dayTime[1]);

        int dateState = common.checkDate(dayTime[0]);
        switch (dateState){
            case 1:
                clickableElement.setBackgroundColor(Color.argb(130, 255, 165, 0));
                break;
            case 2:
                clickableElement.setBackgroundColor(Color.argb(130, 0, 0, 255));
                break;
            case 0:
                clickableElement.setBackgroundColor(Color.argb(130, 255, 0, 0));
                break;
            default:
                clickableElement.setBackgroundColor(Color.argb(130, 255, 255, 255));
                break;
        }

        // Set an onClickListener for the clickable element
        clickMe.setOnClickListener(v -> {
            // Handle the click event here
            int dataID = nameTextView.getId();
            common.dataID = dataKey.get(dataID);
            context.startActivity(new Intent(context, CiphData.class));
        });

        // Add the clickable element to the parent layout
        parentLayout.addView(clickableElement);
    }

    private void clearViewChild() {
        // Remove all views from the parent layout
        parentLayout.removeAllViews();
    }

    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        FragmentCiph.context = context;
    }
}