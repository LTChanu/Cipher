package com.home.cipher;

import android.annotation.SuppressLint;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

public class PopupAddMenu {
    PopupWindow popupWindow;
    TextView addTask, addMeet, addGroup, leaveGroup, changeName, createGroup, howToUse, about, feedback;



    public PopupAddMenu(LayoutInflater li, Window w){
        // Inflate the popup window layout
        @SuppressLint("InflateParams") View popupView = li.inflate(R.layout.popup_add_menu, null);

        // Find the views within the popup window layout
        addTask = popupView.findViewById(R.id.addTask);
        addMeet = popupView.findViewById(R.id.addMeet);
        addGroup = popupView.findViewById(R.id.addGroup);
        leaveGroup = popupView.findViewById(R.id.leaveGroup);
        changeName = popupView.findViewById(R.id.changeName);
        createGroup = popupView.findViewById(R.id.createGroup);
        howToUse = popupView.findViewById(R.id.how_to_use);
        about = popupView.findViewById(R.id.about);
        feedback = popupView.findViewById(R.id.feedback);

        // Create the popup window
        popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);

        // Show the popup window at the center of the screen
        popupWindow.showAtLocation(w.getDecorView(), Gravity.NO_GRAVITY, 0, 0);
    }
}
