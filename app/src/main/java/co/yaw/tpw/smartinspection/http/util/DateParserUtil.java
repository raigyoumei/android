package co.yaw.tpw.smartinspection.http.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by leixiaoming on 2018/04/04.
 */

public class DateParserUtil {
    final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Date parse(String dateString) {
        Date date = null;
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        return date;
    }

    public static String toString(Date date) {
        String dateString = sdf.format(date);
        return dateString;
    }
}
