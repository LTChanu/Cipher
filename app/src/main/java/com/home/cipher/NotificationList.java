package com.home.cipher;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

public class NotificationList {
    private final ArrayList<String> notify;
    public NotificationList(String startDateStr, String endDateStr) throws ParseException {
        notify = new ArrayList<>();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        // Parse the start and end dates
        Date startDate = sdf.parse(startDateStr);
        Date endDate = sdf.parse(endDateStr);

        // Create a calendar instance and set it to the start date
        Calendar calendar = Calendar.getInstance();
        assert startDate != null;
        calendar.setTime(startDate);

        // Clear the time component
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Loop through each day until the end date
        while (calendar.getTime().before(endDate) || calendar.getTime().equals(endDate)) {
            // Get the current date
            Date currentDate = calendar.getTime();
            String currentDateStr = sdf.format(currentDate);

            String startHHmm = sdf.format(startDate).substring(11, 16);
            String endHHmm = sdf.format(startDate).substring(11, 16);

            String time1HHmm = sdf.format(Objects.requireNonNull(sdf.parse("01/01/2023 08:00"))).substring(11, 16);
            String time2HHmm = sdf.format(Objects.requireNonNull(sdf.parse("01/01/2023 12:00"))).substring(11, 16);
            String time3HHmm = sdf.format(Objects.requireNonNull(sdf.parse("01/01/2023 19:00"))).substring(11, 16);
            if(startDate.equals(currentDate)){
                int comparisonResult = startHHmm.compareTo(time1HHmm);
                if (comparisonResult <= 0) {
                    addNotify(currentDateStr,randomAdd(1));
                }
                comparisonResult = startHHmm.compareTo(time2HHmm);
                if (comparisonResult <= 0){
                    addNotify(currentDateStr,randomAdd(2));
                }
                comparisonResult = startHHmm.compareTo(time3HHmm);
                if (comparisonResult <= 0){
                    addNotify(currentDateStr,randomAdd(3));
                }
            }
            time1HHmm = sdf.format(Objects.requireNonNull(sdf.parse("01/01/2023 09:00"))).substring(11, 16);
            time2HHmm = sdf.format(Objects.requireNonNull(sdf.parse("01/01/2023 13:00"))).substring(11, 16);
            time3HHmm = sdf.format(Objects.requireNonNull(sdf.parse("01/01/2023 20:00"))).substring(11, 16);
            assert endDate != null;
            if(endDate.equals(currentDate)){
                int comparisonResult = endHHmm.compareTo(time1HHmm);
                if (comparisonResult >= 0) {
                    addNotify(currentDateStr,randomAdd(1));
                }
                comparisonResult = endHHmm.compareTo(time2HHmm);
                if (comparisonResult >= 0){
                    addNotify(currentDateStr,randomAdd(2));
                }
                comparisonResult = endHHmm.compareTo(time3HHmm);
                if (comparisonResult >= 0){
                    addNotify(currentDateStr,randomAdd(3));
                }
            }

            addNotify(currentDateStr,randomAdd(1));
            addNotify(currentDateStr,randomAdd(2));
            addNotify(currentDateStr,randomAdd(3));

            // Move to the next day
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        assert endDate != null;
        calendar.setTime(endDate);
        calendar.add(Calendar.MINUTE, -20);
        notify.add(sdf.format(calendar.getTime()));
    }

    public ArrayList<String> getNotificationList(){
        return notify;
    }

    private int[] randomAdd(String one, String two){
        String[] startParts = one.split(":");
        String[] endParts = two.split(":");

        int startHour = Integer.parseInt(startParts[0]);
        int startMinute = Integer.parseInt(startParts[1]);
        int endHour = Integer.parseInt(endParts[0]);
        int endMinute = Integer.parseInt(endParts[1]);

        Random random = new Random();

        int randomHour = random.nextInt(endHour - startHour + 1) + startHour;
        int randomMinute = random.nextInt(60);

        if (randomHour == startHour) {
            randomMinute = Math.max(randomMinute, startMinute);
        } else if (randomHour == endHour) {
            randomMinute = Math.min(randomMinute, endMinute);
        }

        return new int[]{randomHour, randomMinute};
    }

    private  int[] randomAdd(int i){
        switch (i){
            case 1:
                return randomAdd("08:00","09:00");
            case 2:
                return randomAdd("12:00","13:00");
            case 3:
                return randomAdd("19:00","20:00");
            default:
                return new int[]{20,0};
        }
    }

    private void addNotify(String value, int[] time) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        // Parse the start and end dates
        Date date = sdf.parse(value);

        // Create a calendar instance and set it to the start date
        Calendar calendar = Calendar.getInstance();
        assert date != null;
        calendar.setTime(date);

        // Clear the time component
        calendar.set(Calendar.HOUR_OF_DAY, time[0]);
        calendar.set(Calendar.MINUTE, time[1]);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Get the updated date value
        Date updatedDate = calendar.getTime();

        // Format the updated date value to the desired format
        String updatedValue = sdf.format(updatedDate);

        // Add the updated value to the notify collection
        notify.add(updatedValue);
    }

}
