package com.example.kevin.health.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by hyx on 2017/4/18.
 */

public class Tools {

    /**
     * 工具类**/



    public static String formatTime(Date date, String format) {
        SimpleDateFormat myFormatter = new SimpleDateFormat(format, Locale.CHINA);
        return myFormatter.format(date);
    }


    public static String formatTime(String datetime, String beforeformat, String afterformat) {
        SimpleDateFormat myFormatter = new SimpleDateFormat(beforeformat, Locale.CHINA);
        String time = "";
        try {
            time = formatTime(myFormatter.parse(datetime), afterformat);
        } catch (Exception e) {
            return time;
        }
        return time;
    }


}
