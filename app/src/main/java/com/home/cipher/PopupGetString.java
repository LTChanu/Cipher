package com.home.cipher;

import android.annotation.SuppressLint;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

public class PopupGetString {
    PopupWindow popupWindow;
    final Window w;
    final View popupView;
    final EditText text;
    final TextView title;
    final TextView statement;
    final Button ok;
    final Button cansel;

    @SuppressLint("InflateParams")
    public PopupGetString(LayoutInflater li, Window w) {
        // Inflate the popup window layout
        this.w = w;
        popupView = li.inflate(R.layout.popup_get_string, null);

        // Find the views within the popup window layout
        text = popupView.findViewById(R.id.string_text);
        title = popupView.findViewById(R.id.title);
        statement = popupView.findViewById(R.id.statement);
        ok = popupView.findViewById(R.id.okButton);
        cansel = popupView.findViewById(R.id.cancelButton);
    }

    public void showAndWaitForInput(String sTitle, String sStatement, String sHint) {
        // Inflate the popup window layout and set up the views
        // Set text
        text.setHint(sHint);
        title.setText(sTitle);
        statement.setText(sStatement);


        // Create the popup window
        popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);

        // Show the popup window at the center of the screen
        popupWindow.showAtLocation(w.getDecorView(), Gravity.CENTER, 0, 0);
    }
}
