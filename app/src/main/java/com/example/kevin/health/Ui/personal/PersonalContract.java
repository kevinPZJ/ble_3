package com.example.kevin.health.Ui.personal;

import android.graphics.Bitmap;

import com.example.kevin.health.base.BaseContract;

/**
 * Created by hyx on 2017/2/7.
 */

public interface PersonalContract  {
    interface View extends BaseContract.BaseView<Presenter>{




        void setHeadImage(Bitmap bitmap);

        void setSport_goal();

        void setWeight_goal();




    }

    interface Presenter extends BaseContract.BasePresenter{

    }
}
