package jp.bo.bocc.helper;

import jp.bo.bocc.controller.web.request.BaseNGRequest;
import jp.bo.bocc.enums.NgEnum;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.quartz.CronExpression;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Namlong on 4/1/2017.
 */
public class ConverterUtils {

    private final static Logger LOGGER = Logger.getLogger(ConverterUtils.class.getName());

    private final static String DATE_FORMAT = "yyyy/MM/dd HH:mm";

    /**
     * get long value
     *
     * @param obj
     * @return
     */
    public static Long getLongValue(Object obj) {
        if (obj != null && NumberUtils.isDigits(obj.toString())) {
            return Long.valueOf(obj.toString());
        }
        return null;
    }

    /**
     * get local date time
     *
     * @param obj
     * @return
     */
    public static LocalDateTime getLocalDateTime(Object obj) {
        if (obj != null)
            if (obj instanceof Timestamp) {
                return ((Timestamp) obj).toLocalDateTime();
            } else
            return (LocalDateTime) obj;
        else
            return null;
    }

    public static String convertReasonCodeToString(BaseNGRequest baseNGRequest) {
        String title = "";
        if (baseNGRequest.getNotSuitable() != null)
            title = NgEnum.NOT_SUITABLE.ordinal() + "";
        if (baseNGRequest.getSensitiveInf() != null)
            title = title + ", " + NgEnum.SENSITIVE_INF.ordinal();
        if (baseNGRequest.getSlander() != null)
            title = title + ", " + NgEnum.SLANDER.ordinal();
        if (baseNGRequest.getCheat() != null)
            title = title + ", " + NgEnum.CHEAT.ordinal();
        if (baseNGRequest.getOther() != null)
            title = title + ", " + NgEnum.OTHER.ordinal();

        if (title.startsWith(","))
            title = title.substring(1, title.length());
        title = title.trim();

        return title;
    }

    /**
     * get int value
     *
     * @param obj
     * @return
     */
    public static Integer getIntValue(Object obj) {
        if (obj != null && NumberUtils.isDigits(obj.toString())) {
            return Integer.valueOf(obj.toString());
        }
        return null;
    }

    /**
     * Format datetime with format: yyyy-MM-dd HH:mm
     *
     * @param str
     * @return
     */
    public static LocalDateTime formatLocalDateTime(String str) {
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(str)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
            return dateTime;
        }
        return null;
    }

    /**
     * convert date to string yyyy/MM/dd HH:mm
     * @param date
     * @return
     */
    public static String convertDateToString(Date date){
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String dateStr = df.format(date);
        return dateStr;
    }

    /**
     * convert string to localdatetime.
     * if str is not valid time, return now.
     *
     * @param str
     * @return
     */
    public static LocalDateTime convertStringToLocaldatetime(String str) {
        LocalDateTime result = LocalDateTime.now();
        try {
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(str))
                result = LocalDateTime.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("ERROR: Convert string to localdatetime error");
        }
        return result;
    }

    /**
     * convert object having timestamp data type to localdatetime.
     * if str is not valid time, return null.
     *
     * @param obj
     * @return
     */
    public static LocalDateTime convertTimestampToLocaldatetime(Object obj) {
        LocalDateTime result = null;
        try {
            if (obj != null)
                result = ((Timestamp) obj).toLocalDateTime();
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("ERROR: Convert string to localdatetime error");
        }
        return result;
    }

    /**
     * Convert Date to LocalDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime convertDateToDateTime(Date date) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            String dateValue = sdf.format(date);
            LocalDateTime ldt = LocalDateTime.parse(dateValue, DateTimeFormatter.ofPattern(DATE_FORMAT));
            return ldt;
        } else {
            return null;
        }
    }

    public static long convertBigDecimalToLong(Object obj){
        if (obj == null)
            return 0;
        else
            return new BigDecimal(obj.toString()).longValue();
    }

    public static int convertBigDecimalToInt(Object obj){
        if (obj instanceof BigDecimal){
            return new BigDecimal(obj.toString()).intValue();
        }
        return 0;
    }

    /**
     * convert resultList to list<Long>
     * @param objs
     * @return
     */
    public static List<Long> convertResultListToListLong(List<Object> objs){
        List<Long> result = new ArrayList<>();
        for(Object obj : objs){
            if (obj instanceof BigDecimal){
                result.add(new BigDecimal(obj.toString()).longValue());
            }
        }
        return result;
    }

    /**
     * Return result with format: yyyy-MM-dd HH:mm:ss
     * @param localDateTime
     * @return
     */
    public static String formatLocalDateTimeToStr(LocalDateTime localDateTime) {
        if (localDateTime != null ) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return localDateTime.format(formatter);
        }
        return "";
    }

    /**
     * convert string safe. If the argument is null, return empty string.
     * @param obj
     * @return
     */
    public static String convertStringSafe(Object obj){
        if(obj != null && org.apache.commons.lang3.StringUtils.isNotEmpty(obj.toString())){
            return obj.toString();
        }else {
            return "";
        }
    }

    /**
     * Return cron expression s m H D M ? Y
     * @param runTime
     * @return
     * @throws ParseException
     */
    public static CronExpression buildCronExpression(LocalDateTime runTime) throws ParseException {
        CronExpression cronExpression =
                new CronExpression(
                        runTime.getSecond() + " " + runTime.getMinute() + " "
                        + runTime.getHour() + " " + runTime.getDayOfMonth() + " "
                        + runTime.getMonthValue() + " " + "? " + runTime.getYear()
        );
        return cronExpression;
    }

    public static List<Long> convertStringToListLong(String patrolAdminId) {
        List<Long> result = new ArrayList<>();
        String[] split = patrolAdminId.split(",");
        for(String s : split) {
            if (NumberUtils.isDigits(s))
            result.add(Long.valueOf(s.trim()));
        }
        return result;
    }

    public static String jsonify(Object androidMessageMap) {
        try {
            return new ObjectMapper().writeValueAsString(androidMessageMap);
        } catch (Exception e) {
            e.printStackTrace();
            throw (RuntimeException) e;
        }
    }

    public static long getLastValueAfterUnderline(String name, String symbol) {
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(name)) {
            final int lastIndex = name.lastIndexOf(symbol);
            return Long.valueOf(name.substring(lastIndex + 1));
        }
        return 0;
    }

    public static long convertLongFromString(String str) {
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(str)) {
            return Long.valueOf(str);
        }
        return 0;
    }

    /**
     * parser string having json format to StringEntity object
     * @param jsonData
     * @return
     * @throws Exception
     */
    public static StringEntity buildEntityFromJsonData(String jsonData ) throws Exception {

        StringEntity result = null;
        try {
            result = new StringEntity( jsonData, "UTF-8" );
        } catch( Throwable t ) {
            String msg = "unable to create entity from data; data was: " + jsonData;
            LOGGER.error( msg );
            throw new Exception( msg, t );
        }

        return result;
    }

}