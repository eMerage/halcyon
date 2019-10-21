package com.halcyon.helpModel;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Puritha Dev on 3/19/2015.
 */
public class DateFormat {


    private Context context;

    public DateFormat(Context context) {
        this.context = context;
    }

    /**
     * @return current Date Time String
     */
    public String getCurrentDateFormat() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy:HH :mm");
        String currentDateTimeString = sdf.format(new Date()).toString();
        return currentDateTimeString;

    }

    /**
     * @return current Date  String
     */
    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        String currentDateTimeString = sdf.format(new Date()).toString();
        return currentDateTimeString;

    }

    /**
     * get the month name base on the month number
     *
     * @param dateString
     * @return month name
     */
    public static String getMonthName(String dateString) {
        String monthName;

        String monthDateYearArray[] = dateString.split("/");
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
        cal.set(Calendar.MONTH, Integer.parseInt(monthDateYearArray[0]) - 1);
        monthName = month_date.format(cal.getTime());
        return monthName;
    }

    /**
     * calculate the next  month date base on current date
     *
     * @param dateWithTime
     * @return next  month date string
     */
    public static String setNextVisitDate(String dateWithTime) {

        String newDate[] = dateWithTime.split(":");
        String newDate1[] = newDate[0].split("/");
        int mYear = Integer.parseInt(newDate1[2]);
        int mMonth = Integer.parseInt(newDate1[0]);
        int mDay = Integer.parseInt(newDate1[1]);

        String currentDateTimeString;
        if (mMonth == 12) {
            mMonth = 1;
            int mYear1 = mYear + 1;
            currentDateTimeString = mMonth + "/" + mDay + "/" + mYear1;
        } else if (mMonth == 1) {
            if (mDay == 31) {
                mMonth = 3;
                mDay = 3;
            } else if (mDay == 30) {
                mMonth = 3;
                mDay = 2;
            } else if (mDay == 29) {
                mMonth = 3;
                mDay = 1;

            } else {

                mMonth = mMonth + 1;

            }

            currentDateTimeString = mMonth + "/" + mDay + "/" + mYear;
        } else if (mMonth == 3 || mMonth == 5 || mMonth == 7 || mMonth == 8 || mMonth == 10
                ) {
            if (mDay == 31) {

                mDay = 1;
            }
            if (mDay == 30) {

                mDay = 30;
            } else {

                mDay = mDay + 1;
            }
            currentDateTimeString = mMonth + 1 + "/" + mDay + "/" + mYear;
        } else {

            int mMonth1 = mMonth + 1;
            currentDateTimeString = mMonth1 + "/" + mDay + "/" + mYear;
        }

        return currentDateTimeString;


    }

    public static Date getNetMonth(int monthsfromNow) {


        Calendar cal = Calendar.getInstance(); //Get the Calendar instance
        cal.add(Calendar.MONTH, +3);//Three months from now
        Date date = cal.getTime();// Get the Date object

        return date;

    }

}
