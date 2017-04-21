package com.example.kevin.health.base;


import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.example.kevin.health.R;
import com.example.kevin.health.Ui.internal.SnackBarDelegate;
import com.example.kevin.health.Ui.internal.ToolbarDelegate;
import com.example.kevin.health.utils.L;


/**
 * Created by hyx on 2017/2/6.
 */

public class BaseActivity extends AppCompatActivity {

    protected ToolbarDelegate mToolbarDelegate;
    protected SnackBarDelegate mSnackBarDelegate;
    protected View frameLayout;
    private Bundle saveInstanceState;
    protected View  toolBarView;
    protected AppBarLayout appBarLayout;


    @Override
    @CallSuper
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.saveInstanceState = savedInstanceState;
    }

    @Override
    @CallSuper
    public void setContentView( int layoutResID) {
        super.setContentView(layoutResID);
        setupToolbar();
        setupSnackBar();
    }


    @Override
    @CallSuper
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        setupToolbar();
        setupSnackBar();
    }


    public void setMainFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.frame_layout, fragment, tag)
                .commit();
    }

    /**
     * 不需要返回
     *
     * @param fragment
     * @param tag
     */
    public void replaceMainFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_layout, fragment, tag)
                .commit();
    }

    /**
     * 需要返回
     *
     * @param fragment
     * @param tag
     */
    public void replaceMainFragmentInTask(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_layout, fragment, tag)
                .addToBackStack(tag).commit();
    }

    public void addMainFragmentInTask(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.frame_layout, fragment, tag).addToBackStack(tag)
                .commit();
    }

    public void addMainFragmentInTask(Fragment showFragment, Fragment hideFragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (hideFragment.isAdded()) {
            fragmentManager.beginTransaction().hide(hideFragment).add(R.id.frame_layout, showFragment, tag)
                    .addToBackStack(tag).commit();
        }
    }

    public void popMainFragmentFromTask() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
    }

    public void popMainFragmentFromTask(String tag, int flags) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStackImmediate(tag, flags);
        int backStackEntryCount = fragmentManager.getBackStackEntryCount();
        L.d("WTF", backStackEntryCount + "");
    }

    @Nullable
    public Fragment findMainFragment() {
        return getSupportFragmentManager()
                .findFragmentById(R.id.frame_layout);
    }

    @Nullable
    public Fragment findMainFragmentByTag(@NonNull String tag) {
        return getSupportFragmentManager()
                .findFragmentByTag(tag);
    }

    private void setupToolbar() {
        appBarLayout= (android.support.design.widget.AppBarLayout) findViewById(R.id.AppBarLayout );
         toolBarView = findViewById(R.id.toolbar);
        if (toolBarView != null) {
            Toolbar toolbar = (Toolbar) toolBarView;
            mToolbarDelegate = new ToolbarDelegate(this, toolbar);
        }


    }

    private void setupSnackBar() {
        frameLayout = findViewById(R.id.frame_layout);
        if (frameLayout != null)
            mSnackBarDelegate = new SnackBarDelegate(frameLayout);
    }
    @Override
    public void setTitle(CharSequence title) {
        if (mToolbarDelegate == null)
            super.setTitle(title);
        else {
            super.setTitle(null);
            mToolbarDelegate.setTitle(title);
        }
    }

    public void setTitle(CharSequence title, @ToolbarDelegate.BAR_COLOR int color) {
        if (mToolbarDelegate == null)
            super.setTitle(title);
        else {
            super.setTitle(null);
            mToolbarDelegate.setTitle(title, color);
        }
    }

    public void setToolbarBackground(@ColorInt int color) {
        if (mToolbarDelegate != null) {
            mToolbarDelegate.setToolBarBackground(color);
        }
    }

    public void setToolbarBackIcon(@ToolbarDelegate.NAV_ICON int icon, @ToolbarDelegate.BAR_COLOR int color) {
        if (mToolbarDelegate != null) {
            mToolbarDelegate.setupNavCrossIcon(icon, color);
        }

    }
    public SnackBarDelegate getSnackBarDelegate() {
        return mSnackBarDelegate;
    }

    public void removeToolbar(){

        appBarLayout.setVisibility(View.GONE);

    }
    public void showToolbar(){
        appBarLayout.setVisibility(View.VISIBLE);
    }

    @Override
    @CallSuper
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
