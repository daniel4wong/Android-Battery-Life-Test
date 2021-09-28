package com.daniel4wong.AndroidBatteryLifeTest.Helper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FormatHelper {
    public static String dateToString() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
    public static String dateToString(Date data) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(data);
    }
    public static String dateToString(Long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return dateToString(calendar.getTime());
    }
}
