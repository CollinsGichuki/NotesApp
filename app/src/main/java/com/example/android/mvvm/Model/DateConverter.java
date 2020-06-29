package com.example.android.mvvm.Model;

import androidx.room.TypeConverter;

import java.util.Date;

public class DateConverter {
    @TypeConverter
    public static Date timeStampToCalendar(Long value){
        return value == null ? null : new Date(value);
    }
    @TypeConverter
    public static Long dateToTimeStamp(Date date){
        return date == null ? null :  date.getTime();
    }
}
