package com.home.cipher;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedVariable {
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String KEY_IS_LOGIN = "isLogin";//must update with login & Register & Logout
    private static final String KEY_DB_EMAIL = "dbEmail";//must update with login & Register
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

    public boolean getIsLogIn() {
        SharedPreferences prefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_IS_LOGIN, false);
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

}

//    SharedVariable sharedVariable = new SharedVariable(this);
//    sharedVariable.setIsParent(true); // Set the boolean value to true
//
//        SharedVariable sharedVariable = new SharedVariable(this);
//        boolean isParent = sharedVariable.isParent(); // Get the boolean value
