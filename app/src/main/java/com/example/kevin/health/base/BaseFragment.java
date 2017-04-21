package com.example.kevin.health.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.kevin.health.R;
import com.example.kevin.health.ServerException;
import com.example.kevin.health.Ui.internal.SnackBarDelegate;
import com.example.kevin.health.Ui.internal.ToolbarDelegate;
import com.example.kevin.health.utils.L;


/**
 * Created by hyx on 2017/2/6.
 */

public class BaseFragment extends Fragment {

    protected BaseActivity baseActivity;
    protected
    @Nullable
    SnackBarDelegate mSnackBarDelegate;


    @Override
    @CallSuper
    public void onAttach(Context context) {
        super.onAttach(context);


        if (context instanceof BaseActivity) {
            baseActivity = (BaseActivity) context;
            mSnackBarDelegate = baseActivity.getSnackBarDelegate();
        } else {
            throw new IllegalStateException("Activity Should extends BaseActivity");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setTitle(CharSequence title) {
        baseActivity.setTitle(title);
    }
    public void setTitle(CharSequence title,@ToolbarDelegate.BAR_COLOR int color){
        baseActivity.setTitle(title,color);
    }
    public void setToolbarBackground(@ColorInt int color){
        baseActivity.setToolbarBackground(color);
    }
    public void setToolbarBackIcon(@ToolbarDelegate.NAV_ICON int icon,@ToolbarDelegate.BAR_COLOR int color){
        baseActivity.setToolbarBackIcon(icon,color);
    }
    public void setTitle(@StringRes int titleId) {
        baseActivity.setTitle(titleId);
    }

    public void showToast(String msg) {
        L.w(msg);
        InputMethodManager imm = (InputMethodManager) baseActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(baseActivity.getWindow().getDecorView().getWindowToken(), 0);
        }
        if (mSnackBarDelegate != null) {
            mSnackBarDelegate.showShortText(msg);
        }
        else
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void showToast(@StringRes int msg) {
        showToast(baseActivity.getString(msg));
    }

    public void showToast(Throwable e) {
        L.e(e);
        if (e instanceof ServerException)
            showToast(e.getMessage());
        else
            showToast(R.string.error_network_state);
    }

    public Bundle getIntentBundle(){
        return getArguments();
    }

    public void finish() {
        baseActivity.finish();
    }

}
