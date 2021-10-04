package com.daniel4wong.core.Model.TypeConverter;

import androidx.room.TypeConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTypeConverter {
    static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");

    @TypeConverter
    public Date toDate(String dateString) {
        if(dateString == null) {
            return null;
        } else {
            try {
                return df.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @TypeConverter
    public String fromDate(Date date) {
        if (date == null) {
            return null;
        } else {
            return df.format(date);
        }
    }
}