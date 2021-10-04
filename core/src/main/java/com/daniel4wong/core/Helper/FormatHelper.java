package com.daniel4wong.core.Helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FormatHelper {
    public static final String format = "yyyy-MM-dd HH:mm:ss";
    public static final String format_NO_MINUTES = "yyyy-MM-dd HH:mm";

    public static String dateToString() {
        return new SimpleDateFormat(format).format(new Date());
    }
    public static String dateToString(Date data) {
        return new SimpleDateFormat(format).format(data);
    }
    public static String dateToString(Long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return dateToString(calendar.getTime());
    }
    public static Date stringToDate(String text) {
        return stringToDate(text, format_NO_MINUTES);
    }
    public static Date stringToDate(String text, String format) {
        try {
            return new SimpleDateFormat(format).parse(text);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
