package com.home.cipher;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import androidx.exifinterface.media.ExifInterface;
import androidx.exifinterface.media.ExifInterfaceCompat;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class common {

    private static ProgressDialog progressDialog;
    public static String description, server, rDBEmail, dataID, AlarmName, taskName, taskDescription, startTime, notifyTime, link;
    public static boolean edit = false, tips=true;
    public static ArrayList<Bitmap> ImageBitmap = new ArrayList<>();
    public static ArrayList<String> ImageID = new ArrayList<>();
    public static ArrayList<Integer> ImageOrientation = new ArrayList<>();
    public static int fragmentNumber;
    public static DataSnapshot snapshot = null;

    public static void getImageOrientation(byte[] imageBytes) {
        try {
            // Convert the byte array to an InputStream
            InputStream inputStream = new ByteArrayInputStream(imageBytes);

            // Create an ExifInterface instance from the InputStream
            ExifInterface exifInterface = new ExifInterface(inputStream);

            // Retrieve the orientation attribute
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            // Use the orientation value as needed
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    ImageOrientation.add(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    ImageOrientation.add(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    ImageOrientation.add(270);
                    break;
                // Add more cases if needed

                default:
                    ImageOrientation.add(0);
                    break;
            }

            // Close the InputStream
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showMessage(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            dialog.dismiss(); // dismisses the dialog
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

    public static void startLoading(Context context, String title) {
        progressDialog = ProgressDialog.show(context, title, "Please wait...", false, false);
    }

    public static void stopLoading() {
        progressDialog.dismiss(); // = ProgressDialog.show(context,title,"Please wait...",false,false);
    }

    public static String getCDateTime() {
        Date now = new Date();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(now);
    }

    public static String getCDateTimeString() {
        Date now = new Date();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd   HH:mm");
        return dateFormat.format(now);
    }

    public static String convertToDate(String dateTimeStr) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputTimeFormat = new SimpleDateFormat("yyyy.MM.dd");
        Date dateTime;
        try {
            dateTime = inputDateFormat.parse(dateTimeStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        assert dateTime != null;
        return outputTimeFormat.format(dateTime);
    }

    public static String convertToTime(String dateTimeStr) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputTimeFormat = new SimpleDateFormat("HH:mm");
        Date dateTime;
        try {
            dateTime = inputDateFormat.parse(dateTimeStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        assert dateTime != null;
        return outputTimeFormat.format(dateTime);
    }

    public static String convertToTimeFormat(String dateTimeStr) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputTimeFormat = new SimpleDateFormat("HH:mm");
        Date dateTime;
        try {
            dateTime = inputDateFormat.parse(dateTimeStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        assert dateTime != null;
        return outputTimeFormat.format(dateTime);
    }

    public static String convertToDateFormat(String dateTimeStr) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputTimeFormat = new SimpleDateFormat("yyyy.MM.dd");
        Date dateTime;
        try {
            dateTime = inputDateFormat.parse(dateTimeStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        assert dateTime != null;
        return outputTimeFormat.format(dateTime);
    }

    public static String convertToDateTimeFormat(String dateTimeStr) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date dateTime;
        try {
            dateTime = inputDateFormat.parse(dateTimeStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        assert dateTime != null;
        return outputTimeFormat.format(dateTime);
    }

    public static String convertToID(String dateTimeStr) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputTimeFormat = new SimpleDateFormat("yyyyMMddHHmm");
        Date dateTime;
        try {
            dateTime = inputDateFormat.parse(dateTimeStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        assert dateTime != null;
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

    public static String[] convertToDayTime(String key) throws ParseException {
        String date = convertToDateFormat(key);
        String time = convertToTimeFormat(key);
        return new String[]{date, time};
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

    public static long covertToTimeInMillis(String inputDateTime) {
        // Create a SimpleDateFormat object for the input format

        Calendar cal = Calendar.getInstance();
        cal.set(getYear(inputDateTime), getMonth(inputDateTime) - 1, getDay(inputDateTime), getHour(inputDateTime), getMinutes(inputDateTime), 0);
        return cal.getTimeInMillis();
    }

    public static long getNowTimeInMillis() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), 0);
        return cal.getTimeInMillis();
    }

    public static int getYear(String inputDateTime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        // Create a SimpleDateFormat object for the output format
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy", Locale.getDefault());

        try {
            // Parse the input date and time string
            Date date = inputFormat.parse(inputDateTime);

            // Format the date object to the desired output format
            assert date != null;
            String outputDateTime = outputFormat.format(date);

            // Use the outputDateTime string as needed
            return Integer.parseInt(outputDateTime); // Output: 2022-12-31 23:59:00
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int getMonth(String inputDateTime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        // Create a SimpleDateFormat object for the output format
        SimpleDateFormat outputFormat = new SimpleDateFormat("MM", Locale.getDefault());

        try {
            // Parse the input date and time string
            Date date = inputFormat.parse(inputDateTime);

            // Format the date object to the desired output format
            assert date != null;
            String outputDateTime = outputFormat.format(date);

            // Use the outputDateTime string as needed
            return Integer.parseInt(outputDateTime); // Output: 2022-12-31 23:59:00
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int getDay(String inputDateTime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        // Create a SimpleDateFormat object for the output format
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd", Locale.getDefault());

        try {
            // Parse the input date and time string
            Date date = inputFormat.parse(inputDateTime);

            // Format the date object to the desired output format
            assert date != null;
            String outputDateTime = outputFormat.format(date);

            // Use the outputDateTime string as needed
            return Integer.parseInt(outputDateTime); // Output: 2022-12-31 23:59:00
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int getHour(String inputDateTime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        // Create a SimpleDateFormat object for the output format
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH", Locale.getDefault());

        try {
            // Parse the input date and time string
            Date date = inputFormat.parse(inputDateTime);

            // Format the date object to the desired output format
            assert date != null;
            String outputDateTime = outputFormat.format(date);

            // Use the outputDateTime string as needed
            return Integer.parseInt(outputDateTime); // Output: 2022-12-31 23:59:00
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int getMinutes(String inputDateTime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        // Create a SimpleDateFormat object for the output format
        SimpleDateFormat outputFormat = new SimpleDateFormat("mm", Locale.getDefault());

        try {
            // Parse the input date and time string
            Date date = inputFormat.parse(inputDateTime);

            // Format the date object to the desired output format
            assert date != null;
            String outputDateTime = outputFormat.format(date);

            // Use the outputDateTime string as needed
            return Integer.parseInt(outputDateTime); // Output: 2022-12-31 23:59:00
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int checkDate(String notifyDate, String deuDate) throws ParseException {
        String nd = convertToDate(notifyDate);
        String dd = convertToDate(deuDate);

        // Create a DateTimeFormatter for the input date format
        DateTimeFormatter inputFormatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            inputFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        }

        // Parse the string into a LocalDate object using the input formatter
        LocalDate date = null;
        LocalDate dDate = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            date = LocalDate.parse(nd, inputFormatter);
            dDate = LocalDate.parse(dd, inputFormatter);
        }

        // Print the date in the desired format
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate givenDate = LocalDate.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth()); // Example date: June 24, 2023
            LocalDate givenDueDate = LocalDate.of(dDate.getYear(), dDate.getMonthValue(), dDate.getDayOfMonth()); // Example date: June 24, 2023

            // Compare the given date with the current date
            if (givenDate.isBefore(givenDueDate)) {
                return 0;
            } else if (givenDate.isEqual(givenDueDate)) {
                return 1;
            } else if (givenDate.isAfter(givenDueDate)) {
                return 2;
            }
        }

        return 3; // Return a default value in case of parsing error
    }

    public static boolean checkTime(String deuDate) throws ParseException {
        String time = convertToTime(deuDate);
        return !time.equals("12:00") && !time.equals("00:00");
    }

    public static boolean isEditable(String dateTimeString) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");

        int d = Integer.parseInt(String.valueOf(snapshot.child("editableTime").getValue()));
        try {
            Date dateTime = format.parse(dateTimeString);

            // Create a Calendar instance and set it to the current date and time
            Calendar currentTime = Calendar.getInstance();
            currentTime.setTime(new Date());

            // Set the parsed date and time to another Calendar instance
            Calendar targetTime = Calendar.getInstance();
            assert dateTime != null;
            targetTime.setTime(dateTime);

            // Add 30 minutes to the target time
            targetTime.add(Calendar.MINUTE, d);

            // Compare the current time with the target time
            return !currentTime.after(targetTime);

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}
