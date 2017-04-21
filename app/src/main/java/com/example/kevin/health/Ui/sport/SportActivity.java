package com.example.kevin.health.Ui.sport;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.example.kevin.health.R;
import com.example.kevin.health.Ui.internal.ToolbarDelegate;
import com.example.kevin.health.base.BaseActivity;
import com.ykrank.library.StatusBarUtil;

/**
 * Created by hyx on 2017/4/19.
 */

public class SportActivity extends BaseActivity {

    public static void StartActivity(Fragment fragment){
        Intent intent =new Intent(fragment.getContext(),SportActivity.class);
        fragment.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        setTitle("运动");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.colorPrimary),0);
            StatusBarUtil.StatusBarLightMode(this);
        } else {
            StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.colorPrimary),100);
        }
        setToolbarBackground(getResources().getColor(R.color.colorPrimary));
        setToolbarBackIcon(ToolbarDelegate.CLOSE,ToolbarDelegate.COLOR_BLACK);


        SportFragment fragment = SportFragment.newInstance();
        if (fragment==null){
            setMainFragment(fragment,"sport");
        }

    }
}
