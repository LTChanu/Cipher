package com.home.cipher;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class MyService extends Service implements SharedVariable.DataSnapshotListener {

    private static final int ALARM_REQUEST_CODE = 100;

    private SharedVariable sharedVariable;
    private final Handler handler = new Handler();
    private boolean createNotification = true, createAlarm = false, changeImage = false;
    private StorageReference storageRef;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (common.isNetworkConnected(this) && !sharedVariable.getServer().equals("null")) {
            FirebaseApp.initializeApp(this);
            DatabaseReference Data = FirebaseDatabase.getInstance().getReference();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            storageRef = storage.getReference();

            Data.child("server/" + sharedVariable.getServer()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    common.snapshot = snapshot;
                    sharedVariable.setDataSnapshot(snapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        if (intent != null && "com.home.cipher.NOTIFY_ALARM".equals(intent.getAction())) {
            Notification();
        }

        ////////////////////////////////////////////////////////////////////////////////////

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "your_channel_id";
            String channelName = "Your Channel Name";
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW); // Set importance to LOW
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            Intent alarmIntent = new Intent(this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            @SuppressLint("NotificationTrampoline") Notification notification = new NotificationCompat.Builder(this, channelId)
                    .setContentTitle("Cipher is running. Don't close")
                    .setContentText("Tap to open the app")
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentIntent(pendingIntent)
                    .setSound(null)
                    .build();

            startForeground(1, notification);
        } else {
            // For devices prior to Android Oreo, start the service as foreground without a notification.
            startForeground(1, new Notification());
        }

        ///////////////////////////////////////////////////////////////////////////////////////

        return START_STICKY;
    } //function OK

    @Override
    public void onCreate() {
        super.onCreate();
        // Perform initialization tasks here
        sharedVariable = new SharedVariable(this);
        sharedVariable.addDataSnapshotListener(this);
    }

    public void setAlarm(long timeInMillis) {
        cancelAlarm();
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
        }
    } //function OK

    public void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    @Override
    public void onDataSnapshotChanged(String dataSnapshot) {
        createAlarm = false;
        changeImage = false;

        handler.postDelayed(() -> {
            // Call the function here
            createAlarm = true;
            changeImage = true;
            // Call the function here
            handler.postDelayed(() -> {
                CreateAlarm();
                updateImage();
            }, 1000);
        }, 100);
    } //function OK

    private void updateImage() {
        if(changeImage){
            common.ImageID.clear();
            common.ImageBitmap.clear();
            common.ImageOrientation.clear();
            for (DataSnapshot user : common.snapshot.child("user").getChildren()){
                if(user.child("img").exists()){
                    storageRef.child(String.valueOf(user.child("img").getValue())).getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
                        // Decode the byte array to a Bitmap
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        // Display the image in an ImageView
                        common.getImageOrientation(bytes);
                        common.ImageBitmap.add(bitmap);
                        common.ImageID.add(user.getKey());
                    });
                    for(int i: common.ImageOrientation) {
                        Log.d("ImageOrientation", String.valueOf(i));
                    }
                }
            }
        }
    }

    private void CreateAlarm() {
        if (!sharedVariable.getServer().equals("null") && createNotification && createAlarm) {
            createAlarm = false;
            common.rDBEmail = sharedVariable.getDBEmail();
            ArrayList<String> notifyValue = new ArrayList<>();
            ArrayList<String> notifyName = new ArrayList<>();
            ArrayList<String> description = new ArrayList<>();
            DataSnapshot snapshot = common.snapshot;

            for (DataSnapshot snap : snapshot.child("meet").getChildren()) {
                for (DataSnapshot gID : snap.child("groups").getChildren()) {
                    if (gID.child(common.rDBEmail).exists()) {
                        if (Objects.equals(gID.child(common.rDBEmail).getValue(), "upcoming")) {
                            for (DataSnapshot i : snap.child("notify").getChildren()) {
                                notifyValue.add(String.valueOf(common.covertToTimeInMillis(String.valueOf(i.getValue()))));
                                notifyName.add(String.valueOf(snap.child("name").getValue()));
                                description.add(String.valueOf(snap.child("description").getValue()));
                            }
                        }
                    }
                }
            }

            for (DataSnapshot snap : snapshot.child("task").getChildren()) {
                for (DataSnapshot gID : snap.child("groups").getChildren()) {
                    if (gID.child(common.rDBEmail).exists()) {
                        if (Objects.equals(gID.child(common.rDBEmail).getValue(), "upcoming")) {
                            for (DataSnapshot i : snap.child("notify").getChildren()) {
                                notifyValue.add(String.valueOf(common.covertToTimeInMillis(String.valueOf(i.getValue()))));
                                notifyName.add(String.valueOf(snap.child("name").getValue()));
                                description.add(String.valueOf(snap.child("description").getValue()));
                            }
                        }
                    }
                }
            }


            if (!notifyValue.isEmpty()) {
                long alarm = Long.MAX_VALUE;
                long now = common.getNowTimeInMillis();
                for (String a : notifyValue) {
                    long x = Long.parseLong(a);
                    if (x > now && x < alarm) {
                        alarm = x;
                    }
                }
                if (alarm != Long.MAX_VALUE) {
                    common.AlarmName = notifyName.get(notifyValue.indexOf(String.valueOf(alarm)));
                    common.description = description.get(notifyValue.indexOf(String.valueOf(alarm)));
                    setAlarm(alarm);
                }
            }
        }
    }

    public void Notification() {
        createNotification = false;
        // Create an Intent for the notification
        Intent intent = new Intent(this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Define the notification channel ID and name
        String channelId = "channel_id";
        String channelName = "Channel Name";

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle(common.AlarmName)
                .setContentText(common.description)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Get a unique notification ID
        int notificationId = sharedVariable.getNotifyID();
        sharedVariable.setNotifyId(notificationId+1);

        // Show the notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the notification channel for Android 8.0 (API level 26) and above
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Channel Description");
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(notificationId, builder.build());

        handler.postDelayed(() -> {
            // Call the function here
            createNotification = true;
            onDataSnapshotChanged("snapshot");
        }, 120000);
    } //function OK

}