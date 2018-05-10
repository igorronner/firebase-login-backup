package com.igorronner.irloginbackup.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by IgorR on 01/05/2017.
 */

public class DateUtils {

    public static final String DATE_FORMAT = "yyyy/MM/dd";


    public static int GetMonth(Date date){

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.MONTH)+1;

    }

    public static int GetHourOfDay(Date date){

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.HOUR_OF_DAY);

    }


    public static int GetMinute(Date date){

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.MINUTE);

    }

    public static int GetDayOfMonth(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.DAY_OF_MONTH);

    }

    public static int GetYear(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.YEAR);

    }

    public static Date GetDateByString(String dateString){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            return simpleDateFormat.parse(dateString);
        } catch (Exception exception){
            return null;
        }
     }

    public static String GetDateDDMMAAAA(String dateString, String separator){
        return GetDateDDMMAAAA(GetDateByString(dateString), separator);
    }

    public static String GetDateAAAAMMDD(String dateString, String separator){
        return GetDateAAAAMMDD(GetDateByString(dateString), separator);
    }

    public static String GetDateAAAAMMDD(Date date, String separator){

        String month = (GetMonth(date) < 10) ? "0"+ GetMonth(date) : GetMonth(date)+"";
        String day = (GetDayOfMonth(date) < 10) ? "0"+ GetDayOfMonth(date) : GetDayOfMonth(date)+"";
        return GetYear(date) + separator + month + separator + day;
    }


    public static String GetDateDDMMAAAA(Date date, String separator){
        String month = (GetMonth(date) < 10) ? "0"+ GetMonth(date) : GetMonth(date)+"";
        String day = (GetDayOfMonth(date) < 10) ? "0"+ GetDayOfMonth(date) : GetDayOfMonth(date)+"";
        return day + separator +  month + separator + GetYear(date);
    }

    public static String GetDateDDMMAAAAHHmm(Date date, String separator){
        String month = (GetMonth(date) < 10) ? "0"+ GetMonth(date) : GetMonth(date)+"";
        String day = (GetDayOfMonth(date) < 10) ? "0"+ GetDayOfMonth(date) : GetDayOfMonth(date)+"";
        String hour =  (GetHourOfDay(date) < 10) ? "0"+ GetHourOfDay(date) : GetHourOfDay(date)+"";
        String minute =  (GetMinute(date) < 10) ? "0"+ GetMinute(date) : GetMinute(date)+"";

        return day + separator +  month + separator + GetYear(date) + ", " + hour +":" + minute;
    }

    public static String GetDateDDMMAAAAHHmm(Long dateInMiles, String separator){
        Date date = new Date();
        date.setTime(dateInMiles);
        return GetDateDDMMAAAAHHmm(date, separator);
    }
}