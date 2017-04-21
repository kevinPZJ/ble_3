package com.example.kevin.health.Ui.heart;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kevin.health.base.BaseFragment;

/**
 * Created by hyx on 2017/4/19.
 */

public class HeartFragment extends BaseFragment {

    public static HeartFragment newInstance() {

        Bundle args = new Bundle();

        HeartFragment fragment = new HeartFragment();
        fragment.setArguments(args);
        return fragment;
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
