package com.example.kevin.ble_3.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;



/**
 * Created by zqh on 2016/12/10  13:11.
 * Email:zqhkey@163.com
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";
    private static final boolean DEBUG = true;

    private static final String PATH =  "/log/";
    private static final String FILE_NAME = "crash";
    private static final String FILE_NAME_SUFFIX = ".txt";
    private static final String ABOLUTE_PATH = PATH + FILE_NAME + FILE_NAME_SUFFIX;
    /**
     * 存储异常和参数信息
     */
    private Map<String, String> paramsMap = new HashMap<>();

    private static CrashHandler sInstance = new CrashHandler();
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;
    private Context mContext;
//    private Subscription mSubscribe;

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return sInstance;
    }

    public void init(Context context) {
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }

    /**
     * 这个是最关键的函数，当程序中有未被捕获的异常，系统将会自动调用#uncaughtException方法
     * thread为出现未捕获异常的线程，ex为未捕获的异常，有了这个ex，我们就可以得到异常信息。
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        // 如果系统提供了默认的异常处理器，则交给系统去结束我们的程序，否则就由我们自己结束自己
        if (!handleException(ex) && mDefaultCrashHandler != null) {
            mDefaultCrashHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Log.e("崩溃性质异常", "error : ", e);
            }
            Log.d(TAG, "killProcess");
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
//                MyActivityManager.getInstance().getCurrentActivity().finishAffinity();
                Process.killProcess(Process.myPid());
            } else {
                Process.killProcess(Process.myPid());
            }

//            Process.killProcess(Process.myPid());

        }

    }
    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            Log.d(TAG, "handleException : null--");
            return false;
        }
        Log.d(TAG, "handleException :" + ex);
        // // 使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "程序即将退出，程序猿正赶往现场....",
                        Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
            // 导出异常信息到SD卡中
            collectDeviceInfo(mContext);
           String exceptString = saveCrashInfo2File(ex);
//            uploadExceptionToServer(exceptString);
        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        //获取versionName,versionCode
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                paramsMap.put("versionName", versionName);
                paramsMap.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        //获取所有系统信息
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                paramsMap.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回错误信息, 便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) {

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try {
            File dir = new File(mContext.getExternalCacheDir()+PATH);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            long current = System.currentTimeMillis();
            String time = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.CHINA)
                    .format(new Date(current));
            File file = new File(mContext.getExternalCacheDir()+PATH + FILE_NAME +time + FILE_NAME_SUFFIX);

            if (!file.exists()) {
                file.createNewFile();
            }
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(sb.toString().getBytes());
                Log.i(TAG, "saveCrashInfo2File: "+sb.toString());
                fos.close();

            return sb.toString();
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }
        return sb.toString();
    }

    /**
     * 提供方法上传异常信息到服务器
     * @param log
     */
//    private void uploadExceptionToServer(String log) {
//        LogCatQuery query = new LogCatQuery();
//        query.setLog(log);
//        BaseRequest<LogCatQuery> request = new BaseRequest<>(query);
//        request.sign(mContext);
//        mSubscribe = App.get().getApiProvider().apiServiceProvider().updateCrash(request)
//                .compose(RxResultTransformer.handleResult())
//                .compose(RxJavaUtil.iOTransformer())
//                .doOnTerminate(() -> {
//                    RxJavaUtil.unsubscribeIfNotNull( mSubscribe);
//                    mSubscribe = null;
//                })
//                .subscribe();
//
//    }


}
