package com.honeywell.fireiot.utils;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Author: Kayla, Ye
 * @Description:
 * @Date:Created in 2:15 PM 1/30/2019
 */
public class DateUtil {

    /**
     * 将日期转化为指定格式的字符串
     * @param date
     * @param pattern
     * @return
     */
    public  static  String DateToString(Date date, String pattern){
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }


    /**
     * Date格式转化String
     * @param date
     * @param pattern
     * @return
     */
    public  static Date stringToDate(String date, String pattern){
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        try {
            return  dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return  null;
    }

    /**
     * 获取当前时间的string 格式
     * @param pattern
     * @return
     */
    public  static String getCurrentDate( String pattern){
        Calendar calendar = Calendar.getInstance();
        DateFormat df = new SimpleDateFormat(pattern);
        return df.format(calendar.getTime());
    }

    /**
     * 获取几天之前的日期
     * @param startDay
     * @param days
     * @param pattern
     * @return
     */
     public static String getAfterDay(String startDay, int days, String pattern, boolean otherZero){

        DateFormat dateFormat = new SimpleDateFormat(pattern);
         try {
             Date  date= dateFormat.parse(startDay);
             Calendar calendar = Calendar.getInstance();
             calendar.setTime(date);
             calendar.add(Calendar.DATE, days);
             if(otherZero){
                 calendar.set(Calendar.HOUR_OF_DAY, 0);
                 calendar.set(Calendar.MINUTE, 0);
                 calendar.set(Calendar.SECOND,0);
             }
             return  dateFormat.format(calendar.getTime());
         } catch (ParseException e) {
             e.printStackTrace();
         }

        return  null;
    }

    /**
     * 获取几天之前的日期
     * @param startDay
     * @param days
     * @param pattern
     * @return
     */
    public static String getBeforeDay(String startDay, int days, String pattern, boolean otherZero){
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        try {
            Date  date= dateFormat.parse(startDay);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.DATE,calendar.get(Calendar.DATE)-days);
            if(otherZero){
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND,0);
            }
            return  dateFormat.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 两种时间间隔
     * @param startDay
     * @param endDay
     * @param pattern
     * @return
     */
    public static int daysBetween(String startDay, String endDay,String pattern){
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = dateFormat.parse(startDay);
            endDate = dateFormat.parse(endDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND,0);
        long startTime = calendar.getTimeInMillis();


        calendar.setTime(endDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND,0);
        long endTime = calendar.getTimeInMillis();

        long days = (endTime - startTime )/(1000*3600*24);
        return Integer.parseInt(String.valueOf(days));

    }


    public static List<Date> getAfterListDate(List<Long> days, String pattern, boolean otherZero){
        String today = getCurrentDate(pattern);
        List<Date> dates = new ArrayList<>();
        for(Long day: days){
            String afterDay = getAfterDay(today, day.intValue(), pattern, otherZero);
            dates.add(stringToDate(afterDay, pattern));
        }

        return  dates;
    }

}
