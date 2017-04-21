package com.example.kevin.health.Ui.internal;

import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.kevin.health.App;
import com.example.kevin.health.R;


/**
 * This class represents a delegate which you can use to add
 * {@link Toolbar} to {@link AppCompatActivity}.
 */
public final class ToolbarDelegate {
    public static final int NONE = 0;
    public static final int UP = 1;
    public static final int CLOSE = 2;
    public static final int COLOR_WHITE = 1;
    public static final int COLOR_BLACK = 2;
    @IntDef({NONE, UP, CLOSE})
    public @interface NAV_ICON {

    }

    @IntDef({COLOR_BLACK, COLOR_WHITE})
    public @interface BAR_COLOR{

    }
    private final AppCompatActivity mAppCompatActivity;
    private final Toolbar mToolbar;
    private final TextView mTitleTextView;

    public ToolbarDelegate(AppCompatActivity appCompatActivity, Toolbar toolbar) {
        this.mAppCompatActivity = appCompatActivity;
        this.mToolbar = toolbar;
        this.mTitleTextView = (TextView) appCompatActivity.findViewById(R.id.toolbar_title);

        setUpToolbar();
    }

    /**
     * Sets a {@link android.widget.Toolbar Toolbar} to act as the {@link android.support.v7.app.ActionBar}
     * for this Activity window.
     * Also displays home as an "up" affordance in Toolbar.
     */
    private void setUpToolbar() {
        // designate a Toolbar as the ActionBar
//        mToolbar.setNavigationIcon(R.drawable.xxt_back_black);
        mAppCompatActivity.setSupportActionBar(mToolbar);
    }

    /**
     * Sets Toolbar's navigation icon to cross.
     */
    public void setupNavCrossIcon(@NAV_ICON int icon,@BAR_COLOR int color) {
        switch (icon) {
            case UP:
                App.AppTool.checkNotNull(mAppCompatActivity.getSupportActionBar())
                        .setDisplayHomeAsUpEnabled(true);
                break;
            case CLOSE:
                App.AppTool.checkNotNull(mAppCompatActivity.getSupportActionBar())
                        .setDisplayHomeAsUpEnabled(true);
                if (color == COLOR_BLACK) {
                    mToolbar.setNavigationIcon(R.drawable.xxt_back_black);
                } else {
                    mToolbar.setNavigationIcon(R.drawable.xxt_back_white);
                }
                break;
            case NONE:
                break;
            default:
                break;
        }
    }
    public void setToolBarBackground(@ColorInt int color){
        mToolbar.setBackgroundColor(color);
    }

    public void setTitle(CharSequence title){
        if (mTitleTextView == null)
            mToolbar.setTitle(title);
        else {
            mToolbar.setTitle(null);
            mTitleTextView.setText(title);
        }
    }
    public void setTitle(CharSequence title,@BAR_COLOR int color){
        setTitle(title);
        switch (color) {
            case COLOR_BLACK:
                mToolbar.setTitleTextColor(App.getApp().getResources().getColor(R.color.title_color));
                mTitleTextView.setTextColor(App.getApp().getResources().getColor(R.color.title_color));
                break;
            case COLOR_WHITE:
                mToolbar.setTitleTextColor(App.getApp().getResources().getColor(R.color.title_color_white));
                mTitleTextView.setTextColor(App.getApp().getResources().getColor(R.color.title_color_white));
                break;
        }
    }
    public Toolbar getToolbar() {
        return mToolbar;
    }
}
