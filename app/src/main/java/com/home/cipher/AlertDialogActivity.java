package com.home.cipher;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AlertDialogActivity extends Activity {

    private static final int ALARM_REQUEST_CODE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the alarm text from the intent
        String alarmText = common.AlarmName;
        String alarmDescription = common.description;

        // Create and show the AlertDialog
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(alarmText)
                .setMessage(alarmDescription)
                .setPositiveButton("OK", (dialog, which) -> {
                    cancelAlarm();
                    stopAlarmSound();
                    dialog.dismiss();
                    finish();
                })
                .create();

        // Set the dialog to be cancelable by pressing the back button
        alertDialog.setCancelable(false);

        // Show the dialog
        alertDialog.show();
    }

    public void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            // Cancel the alarm
            alarmManager.cancel(pendingIntent);
        }
    }

    private void stopAlarmSound() {
        AlarmReceiver.stopAlarmSound();
    }

}
