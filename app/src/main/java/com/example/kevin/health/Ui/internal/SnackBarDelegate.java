package com.example.kevin.health.Ui.internal;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.example.kevin.health.App;
import com.example.kevin.health.R;


public final class SnackBarDelegate {
    private View mView;
    private Snackbar mSnackbar;
    
    public SnackBarDelegate(@NonNull View view){
        mView = view;
    }
    
    public void showShortText(CharSequence text){
        mSnackbar = Snackbar.make(mView, text, Snackbar.LENGTH_SHORT);
        setSnackbarColor(mSnackbar, App.getApp().getResources().getColor(R.color.mainGreen));
        mSnackbar.show();
    }
    
    public void showShortText(@StringRes int resId){
        mSnackbar = Snackbar.make(mView, resId, Snackbar.LENGTH_SHORT);
        mSnackbar.show();
    }
    
    public void showLongText(CharSequence text){
        mSnackbar = Snackbar.make(mView, text, Snackbar.LENGTH_LONG);
        mSnackbar.show();
    }
    
    public void showLongText(@StringRes int resId){
        mSnackbar = Snackbar.make(mView, resId, Snackbar.LENGTH_LONG);
        mSnackbar.show();
    }

    public void dismiss() {
        if (mSnackbar != null && mSnackbar.isShownOrQueued()) {
            mSnackbar.dismiss();
        }
    }
    /**
     * 设置Snackbar背景颜色
     * @param snackbar
     * @param backgroundColor
     */
    public static void setSnackbarColor(Snackbar snackbar, int backgroundColor) {
        View view = snackbar.getView();
        if(view!=null){
            view.setBackgroundColor(backgroundColor);
        }
    }
}
