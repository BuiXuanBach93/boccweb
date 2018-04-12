package jp.bo.bocc.helper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Namlong on 6/9/2017.
 */
public class DateUtils {

    /**
     * Set time for date to - 00:00:00
     *
     * @param date
     * @return
     */
    public static Date setTimeToStartTimeInDay(Date date) {
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            return calendar.getTime();
        }
        return null;
    }

    /**
     * * Set time for date to - 00:00:00
     *
     * @param date
     * @return
     */
    public static Date setTimeToEndTimeInDay(Date date) {
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            return calendar.getTime();
        }
        return null;
    }

    /**
     * Convert localdatetime to Japanese format: {0}年{1}月{2}日
     * @param localDateTime
     * @return
     */
    public static String convertLocalDateTimeToStringWithoutTime(LocalDateTime localDateTime) {
        if (localDateTime != null ) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
            return localDateTime.format(formatter);
        }
        return "";
    }

    /**
     * Convert localdatetime to Japanese format: {0}年{1}月{2}日 HH:mm:ss
     * @param localDateTime
     * @return
     */
    public static String convertLocalDateTimeToStringWithTime(LocalDateTime localDateTime) {
        if (localDateTime != null ) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
            return localDateTime.format(formatter);
        }
        return "";
    }
}
