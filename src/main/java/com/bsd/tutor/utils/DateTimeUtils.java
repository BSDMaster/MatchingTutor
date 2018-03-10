package com.bsd.tutor.utils;

import com.sun.javafx.binding.StringFormatter;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kewalins on 2/17/2018 AD.
 */
public class DateTimeUtils {
    public static Double calculateOverlapTime(Double start1, Double start2, Double end1, Double end2){
        Double totalRange = Math.max(end1, end2) - Math.min(start1, start2);
        Double sumOfRanges = (end1 - start1) + (end2 - start2);
        Double overlappingInterval = 0D;

        if (sumOfRanges > totalRange) { // means they overlap
            overlappingInterval = Math.min(end1, end2) - Math.max(start1, start2);

        }
        return overlappingInterval;

    }

    public static Double getStartTime(Double start1, Double start2){
        return Math.max(start1, start2);
    }

    public static Double getEndTime(Double end1, Double end2){
        return Math.min(end1, end2);
    }

    public static Double convertSecondsToHours(Double seconds){
        return seconds / 3600;
    }

    public static String hourFormat(Date time){
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH.mm");
        return timeFormat.format(time);
    }

    public static Date doubleToDate(Double time) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH.mm");
        String strTime = new DecimalFormat("00.00").format(time).toString();
        try {
            return timeFormat.parse(strTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date addTime(Double time, Double addTime){
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH.mm");
        String strTime = new DecimalFormat("00.00").format(time).toString();
        String strAddTime = new DecimalFormat("00.00").format(addTime).toString();
        try {
            Date convertTime = timeFormat.parse(strTime);
            Date convertAddTime = timeFormat.parse(strAddTime);
            convertTime.setHours( convertTime.getHours() + convertAddTime.getHours());
            convertTime.setMinutes( convertTime.getMinutes() + convertAddTime.getMinutes());
            return convertTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Date addTime( Date time, Double addTime){
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH.mm");
       // String strTime = new DecimalFormat("00.00").format(time).toString();
        String strAddTime = new DecimalFormat("00.00").format(addTime).toString();
        try {
       //     Date convertTime = timeFormat.parse(strTime);
            Date convertAddTime = timeFormat.parse(strAddTime);
            time.setHours( time.getHours() + convertAddTime.getHours());
            time.setMinutes( time.getMinutes() + convertAddTime.getMinutes());
            return time;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Date addTime(Double time, Double addTime1, Double addTime2){
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH.mm");
        String strTime = new DecimalFormat("00.00").format(time).toString();
        String strAddTime1 = new DecimalFormat("00.00").format(addTime1).toString();
        String strAddTime2 = new DecimalFormat("00.00").format(addTime2).toString();
        try {
            Date convertTime = timeFormat.parse(strTime);
            Date convertAddTime1 = timeFormat.parse(strAddTime1);
            Date convertAddTime2 = timeFormat.parse(strAddTime2);
            convertTime.setHours( convertTime.getHours() + convertAddTime1.getHours() + convertAddTime2.getHours() );
            convertTime.setMinutes( convertTime.getMinutes() + convertAddTime1.getMinutes() + convertAddTime2.getMinutes());
            return convertTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Date addTime(Date time, Double addTime1, Double addTime2){
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH.mm");

        String strAddTime1 = new DecimalFormat("00.00").format(addTime1).toString();
        String strAddTime2 = new DecimalFormat("00.00").format(addTime2).toString();
        try {
            Date convertAddTime1 = timeFormat.parse(strAddTime1);
            Date convertAddTime2 = timeFormat.parse(strAddTime2);
            time.setHours( time.getHours() + convertAddTime1.getHours() + convertAddTime2.getHours() );
            time.setMinutes( time.getMinutes() + convertAddTime1.getMinutes() + convertAddTime2.getMinutes());
            return time;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Date addTime(Double time, Double addTime1, Double addTime2, Double addTime3){
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH.mm");
        String strTime = new DecimalFormat("00.00").format(time).toString();
        String strAddTime1 = new DecimalFormat("00.00").format(addTime1).toString();
        String strAddTime2 = new DecimalFormat("00.00").format(addTime2).toString();
        String strAddTime3 = new DecimalFormat("00.00").format(addTime3).toString();
        try {
            Date convertTime = timeFormat.parse(strTime);
            Date convertAddTime1 = timeFormat.parse(strAddTime1);
            Date convertAddTime2 = timeFormat.parse(strAddTime2);
            Date convertAddTime3 = timeFormat.parse(strAddTime3);
            convertTime.setHours( convertTime.getHours() + convertAddTime1.getHours() + convertAddTime2.getHours() + convertAddTime3.getHours());
            convertTime.setMinutes( convertTime.getMinutes() + convertAddTime1.getMinutes() + convertAddTime2.getMinutes() + convertAddTime3.getMinutes());
            return convertTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Date subtractTime(Double time, Double subtractTime){
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH.mm");
        String strTime = new DecimalFormat("00.00").format(time).toString();
        String strSubtractTime = new DecimalFormat("00.00").format(subtractTime).toString();
        try {
            Date convertTime = timeFormat.parse(strTime);
            Date convertSubtractTime = timeFormat.parse(strSubtractTime);
            convertTime.setHours( convertTime.getHours() - convertSubtractTime.getHours());
            convertTime.setMinutes( convertTime.getMinutes() - convertSubtractTime.getMinutes());
            return convertTime;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean inTime(Date time, Date start, Date end) {
        if (!start.after(time) && !time.after(end)) {
            return true;
        }
        return false;
    }


    public static Double[] mergeClassByTime(Double start1, Double end1, Double start2, Double end2, Double duration1, Double duration2){
        Date start = doubleToDate(start1);
        Date end = addTime(start1,duration1,duration2);
        Date dStart1 = doubleToDate(start1);
        Date dStart2 = doubleToDate(start2);
        Date dEnd1 = doubleToDate(end1);
        Date dEnd2 = doubleToDate(end2);

        Double[] time = new Double[2];
        while (!start.before(dStart1) && !end.after(dEnd2)) {
           /*
            System.out.println(" Start :  "+start);
            System.out.println(" End :  "+end);
            System.out.println(" Center :  "+addTime(Double.parseDouble(hourFormat(start)), duration1));

            System.out.println(" 1. INTIME " + inTime(addTime(Double.parseDouble(hourFormat(start)), duration1), dStart2, dEnd2));
            System.out.println(" 2. INTIME " + inTime(end, dStart2, dEnd2));
            System.out.println(" 3. INTIME " + inTime(addTime(Double.parseDouble(hourFormat(start)), duration1), dStart1, dEnd1));
          */
            if (inTime(addTime(Double.parseDouble(hourFormat(start)), duration1), dStart2, dEnd2) && inTime(end, dStart2, dEnd2) && inTime(addTime(Double.parseDouble(hourFormat(start)), duration1), dStart1, dEnd1)) {
          // if (start2 <= end - duration2 && end-duration2 <= end2 && start2 <= end && end <= end2) {
                if (time[0] == null) {
                    time[0] =  Double.parseDouble(hourFormat(start));
                }
                time[1] = Double.parseDouble(hourFormat(end));
            }
            start = addTime( Double.parseDouble(hourFormat(start)), Constants.TIME_DIFF);
            end = addTime( Double.parseDouble(hourFormat(start)),duration1,duration2);

        //    System.out.println(" ==================================== ");
        }

        return time;
    }

}
