package com.example.kevin.health.utils;

import android.util.Log;


/**
 * Created by AdminYkrank on 2016/4/20.
 * 对Log的包装
 */
public class L {
    public static final String LOG_TAG = "RealTimeTravel";

    public static void d(String msg) {
        d(LOG_TAG, msg);
    }

    public static void e(String msg) {
        e(LOG_TAG, msg);
    }

    public static void w(String msg) {
        w(LOG_TAG, msg);
    }

    public static void w(Throwable tr) {
        w(LOG_TAG, tr);
    }

    public static void e(Throwable tr) {
        e(LOG_TAG, tr);
    }

    public static void d(String msg, Throwable tr) {
        d(LOG_TAG, msg, tr);
    }

    public static void i(String msg, Throwable tr) {
        i(LOG_TAG, msg, tr);
    }

    public static void e(String msg, Throwable tr) {
        e(LOG_TAG, msg, tr);
    }

    public static void d(String tag, String msg) {

            Log.d(tag, msg);

    }

    public static void w(String tag, String msg) {

            Log.w(tag, msg);

    }

    public static void e(String tag, String msg) {

            Log.e(tag, msg);

    }

    public static void w(String tag, Throwable tr) {

            Log.w(tag, tr);

    }

    public static void d(String tag, String msg, Throwable tr) {

            Log.d(tag, msg, tr);

    }


    public static void i(String tag, String msg, Throwable tr) {

            Log.i(tag, msg, tr);

    }


    public static void e(String tag, String msg, Throwable tr) {
        if (Log.isLoggable(tag, Log.ERROR)) {
            Log.e(tag, msg, tr);
        }
    }
}
