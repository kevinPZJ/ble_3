package com.example.kevin.health.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringRes;

/**
 * Created by hyx on 2017/2/6.
 */

public interface BaseContract{
    interface BaseView<T>
    {
        Context getContext();

        void showToast(String msg);

        void showToast(@StringRes int msg);

        void finish();

        void showToast(Throwable e);

        Bundle getIntentBundle();
    }

    interface BasePresenter {
        /**
         * 启动时执行
         */
        void start();

        /**
         * 销毁时执行
         */
        void detachView();
    }
}
