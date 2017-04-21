package com.example.kevin.health.utils;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import com.example.kevin.health.App;


/**
 * Created by wuming on 16/10/14.
 */

public class UIUtils {
    private static long lastClickTime;

    public static boolean isDoubleClick() {
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) > 300) {
            lastClickTime = currentClickTime;
            return false;
        }
        return true;
    }

    public static int px2dp(float pxValue) {
        final float scale = App.getApp().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dp2px(float dipValue) {
        final float scale = App.getApp().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2sp(float pxValue) {
        final float fontScale = App.getApp().getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(float spValue) {
        final float fontScale = App.getApp().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    //全局Toast
    public static void showToast(String msg) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(App.getApp().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    //指定界面Toast
    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
