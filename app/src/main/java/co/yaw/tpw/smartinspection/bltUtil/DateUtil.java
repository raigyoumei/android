package co.yaw.tpw.smartinspection.bltUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by leixiaoming on 2018/03/15.
 */

public class DateUtil {

    public static final String YMD_HM = "yyyyMMdd-HHmm";
    public static final String YMD_HMS = "yyyyMMdd-HHmmss";
    public static final String YMDHMSS = "yyyyMMddHHmmss";
    public static final String Y_M_D = "yyyy-MM-dd";
    public static final String H_M_S = "HH:mm:ss";

    public static String getYear() {
        Calendar calendar = getCalendar();
        String year = calendar.get(Calendar.YEAR) + "";

        return year;
    }

    public static String getMonth() {
        Calendar calendar = getCalendar();
        String month = calendar.get(Calendar.MONTH) + 1 + "";

        return month;
    }

    public static String getDay() {
        Calendar calendar = getCalendar();
        String day = calendar.get(Calendar.DAY_OF_MONTH) + "";

        return day;
    }

    public static String getHour() {
        Calendar calendar = getCalendar();
        String hour = calendar.get(Calendar.HOUR_OF_DAY) + "";

        return hour;
    }

    public static String getMinute() {
        Calendar calendar = getCalendar();
        String minute = calendar.get(Calendar.MINUTE) + "";

        return minute;
    }

    public static String getSecond() {
        Calendar calendar = getCalendar();
        String second = calendar.get(Calendar.SECOND) + "";

        return second;
    }

    public static String getCustomTime(String timePattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(timePattern);
        String time = sdf.format(new java.util.Date());

        return time;
    }


    public static String getCustomTime(String timePattern, Long ms) {
        String time = "";
        if(ms != null){
            SimpleDateFormat sdf = new SimpleDateFormat(timePattern);
            if(ms != null){
                try {
                    time = sdf.format(ms);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return time;
    }


    public static String getCustomYMD(String timePattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(timePattern);
        String time = sdf.format(new java.util.Date());

        return time;
    }


    public static String getCustomHMS(String timePattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(timePattern);
        String time = sdf.format(new java.util.Date());

        return time;
    }


    private static Calendar getCalendar() {
        return Calendar.getInstance();
    }
}
