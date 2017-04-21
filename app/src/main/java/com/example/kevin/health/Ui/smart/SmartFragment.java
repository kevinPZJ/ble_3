package com.example.kevin.health.Ui.smart;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kevin.health.R;

/**
 * Created by hyx on 2017/2/6.
 */

public class SmartFragment extends Fragment {

    private RecyclerView recyclerView;

    private SmartContract.Presenter presenter;

    public static SmartFragment newInstance(){
        SmartFragment fragment =new SmartFragment();
        return fragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_smart, container, false);

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        presenter=new SmartPresenter();
        presenter.start();
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }
}
