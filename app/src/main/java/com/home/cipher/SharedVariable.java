package com.home.cipher;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class SharedVariable {
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String KEY_IS_LOGIN = "isLogin";//must update with login & Register & Logout
    private static final String KEY_DB_EMAIL = "dbEmail";//must update with login & Register
    private static final String KEY_DATA_SNAPSHOT = null;
    private static final String KEY_Server = "null";
    private static final String KEY_NOTIFY_ID = "2";
    private static final String KEY_IS_ALARM_RING = "KEY_IS_ALARM_RING";
    private static final String KEY_TIPS = "KEY_TIPS";
    //private static final ArrayList<Long> NOTIFY_DATE = new ArrayList<>();
    private final List<DataSnapshotListener> listeners = new ArrayList<>();
    private final Context mContext;

    public SharedVariable(SignIn signIn) {
        mContext = signIn;
    }

    public SharedVariable(Home home) {
        mContext = home;
    }

    public SharedVariable(Welcome welcome) {
        mContext = welcome;
    }

    public SharedVariable(MyService myService) {
        mContext = myService;
    }

    public SharedVariable(AddGroup addGroup) {
        mContext = addGroup;
    }

    public SharedVariable(LeaveGroup leaveGroup) {
        mContext = leaveGroup;
    }

    public SharedVariable(Sever sever) {
        mContext = sever;
    }

    public SharedVariable(Context context) {
        mContext = context;
    }


    public boolean getIsLogIn() {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_LOGIN, false);
    }

    public boolean getRing() {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_ALARM_RING, true);
    }

    public void setRing(boolean ring){
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_ALARM_RING, ring);
        editor.apply();
    }


    public void setTips(boolean tips){
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_TIPS, tips);
        editor.apply();
    }

    public boolean getTips() {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_TIPS, true);
    }

    public int getNotifyID() {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_NOTIFY_ID, 0);
    }

    public void setNotifyId(int n){
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_NOTIFY_ID, n);
        editor.apply();
    }

    public String getServer() {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_Server, "null");
    }

    public String getDBEmail() {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_DB_EMAIL, "unknown");
    }

    public void setWhileLogin(String dbEmail, boolean isLogIn) {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_DB_EMAIL, dbEmail);
        editor.putBoolean(KEY_IS_LOGIN, isLogIn);
        editor.apply();
    }

    public void setServer(String Started) {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_Server, Started);
        editor.apply();
    }

    public void setDataSnapshot(DataSnapshot dataSnapshot) {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(dataSnapshot.getValue());
        editor.putString(KEY_DATA_SNAPSHOT, json); // Save the JSON string in SharedPreferences
        editor.apply();
        notifyDataSnapshotChanged();
    }

    public void addDataSnapshotListener(DataSnapshotListener listener) {
        listeners.add(listener);
    }

    public void removeDataSnapshotListener(DataSnapshotListener listener) {
        listeners.remove(listener);
    }

    private void notifyDataSnapshotChanged() {
        for (DataSnapshotListener listener : listeners) {
            listener.onDataSnapshotChanged(KEY_DATA_SNAPSHOT);
        }
    }

    public interface DataSnapshotListener {
        void onDataSnapshotChanged(String dataSnapshot);
    }

}

//    SharedVariable sharedVariable = new SharedVariable(this);
//    sharedVariable.setIsParent(true); // Set the boolean value to true
//
//        SharedVariable sharedVariable = new SharedVariable(this);
//        boolean isParent = sharedVariable.isParent(); // Get the boolean value
