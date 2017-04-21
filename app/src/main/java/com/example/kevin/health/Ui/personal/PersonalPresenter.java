package com.example.kevin.health.Ui.personal;

/**
 * Created by hyx on 2017/2/7.
 */

public final class PersonalPresenter implements PersonalContract.Presenter {

    private PersonalContract.View mView;

    public PersonalPresenter(PersonalContract.View view) {

        mView = view;

    }


    @Override
    public void start() {

        getUserDate();

    }


    @Override
    public void detachView() {

    }

    private void getUserDate() {
    }

}

