package com.home.cipher;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;

public class common {

    private static ProgressDialog progressDialog;
    public static String rName, rEmail, rPassword, rType, rDBEmail, dataID, childEmail, parentEmail, nick, taskID; // r mean reuse
    public static boolean isGoogleLogin, isLogin, willSend = false, willGoogleLogin = false, isParent;

    public static void showMessage(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // dismisses the dialog
            }
        });
        builder.show();
    }

    public static String generateOTP() {
        final String NUMBERS = "0123456789";
        final int OTP_LENGTH = 6;

        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < OTP_LENGTH; i++) {
            sb.append(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        }
        return sb.toString();
    }

    public static String hashString(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
        for (byte b : encodedHash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static void startLoading(Context context, String title) {
        progressDialog = ProgressDialog.show(context, title, "Please wait...", false, false);
    }

    public static void stopLoading() {
        progressDialog.dismiss(); // = ProgressDialog.show(context,title,"Please wait...",false,false);
    }

    public static String getCTime() {
        Date now = new Date();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmss");
        return dateFormat.format(now);
    }

    public static String getCDate() {
        Date now = new Date();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(now);
    }

    public static String getCDateTime() {
        Date now = new Date();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(now);
    }

    public static String convertToDate(String dateTimeStr) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputTimeFormat = new SimpleDateFormat("yyyy.MM.dd");
        Date dateTime = null;
        try {
            dateTime = inputDateFormat.parse(dateTimeStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return outputTimeFormat.format(dateTime);
    }
    public static String convertToTime(String dateTimeStr) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputTimeFormat = new SimpleDateFormat("HH:mm");
        Date dateTime = null;
        try {
            dateTime = inputDateFormat.parse(dateTimeStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return outputTimeFormat.format(dateTime);
    }
    public static String convertToTimeFormat(String dateTimeStr) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputTimeFormat = new SimpleDateFormat("HH:mm");
        Date dateTime = null;
        try {
            dateTime = inputDateFormat.parse(dateTimeStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return outputTimeFormat.format(dateTime);
    }

    public static String convertToDateFormat(String dateTimeStr) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputTimeFormat = new SimpleDateFormat("yyyy.MM.dd");
        Date dateTime = null;
        try {
            dateTime = inputDateFormat.parse(dateTimeStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return outputTimeFormat.format(dateTime);
    }

    public static String convertToID(String dateTimeStr) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputTimeFormat = new SimpleDateFormat("yyyyMMddHHmm");
        Date dateTime = null;
        try {
            dateTime = inputDateFormat.parse(dateTimeStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return outputTimeFormat.format(dateTime);
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean r = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if (!r) {
            common.stopLoading();
            Toast.makeText(context, "No internet connection available", Toast.LENGTH_LONG).show();
        }
        return r;
    }

    public static ArrayList<String> removeDuplicate(ArrayList<String> list) {
        HashSet<String> set = new HashSet<>(list);
        list.clear();
        list.addAll(set);
        return list;
    }

    public static String[] convertToDayTime(String key) throws ParseException {
        String date = convertToDateFormat(key);
        String time = convertToTimeFormat(key);
        String[] result = {date,time};
        return result;
    }

    public static int checkDate(String dateString) {
        // Create a DateTimeFormatter for the input date format
        DateTimeFormatter inputFormatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            inputFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        }

        // Parse the string into a LocalDate object using the input formatter
        LocalDate date = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            date = LocalDate.parse(dateString, inputFormatter);
        }

        // Print the date in the desired format
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate givenDate = LocalDate.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth()); // Example date: June 24, 2023

            // Get the current date
            LocalDate currentDate = LocalDate.now();

            // Compare the given date with the current date
            if (givenDate.isBefore(currentDate)) {
                return 0;
            } else if (givenDate.isEqual(currentDate)) {
                return 1;
            } else if (givenDate.isAfter(currentDate)) {
                return 2;
            }
        }

        return 3; // Return a default value in case of parsing error
    }

}
