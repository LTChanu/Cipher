package com.home.cipher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.Settings;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    private static MediaPlayer mp;
    private static boolean ring = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent broadcastIntent = new Intent(context, MyService.class);
        broadcastIntent.setAction("com.home.cipher.NOTIFY_ALARM");
        context.startService(broadcastIntent);

        ring = false;
        SharedVariable sharedVariable = new SharedVariable(context);
        if (sharedVariable.getRing()) {
            ring = true;
            mp = MediaPlayer.create(context, Settings.System.DEFAULT_ALARM_ALERT_URI);
            mp.start();
        }
        Toast.makeText(context, "Wakeup", Toast.LENGTH_LONG).show();

        Intent alertDialogIntent = new Intent(context, AlertDialogActivity.class);
        alertDialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Add this flag to start activity from a BroadcastReceiver
        context.startActivity(alertDialogIntent);
    }

    public static void stopAlarmSound() {
        if (mp != null && ring) {
            mp.stop();
            mp.release();
        }
    }
}

