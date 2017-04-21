package com.example.kevin.health.Ui.sport;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.kevin.health.R;
import com.example.kevin.health.UartService;
import com.example.kevin.health.Ui.step.StepView;
import com.example.kevin.health.base.BaseFragment;
import com.example.kevin.health.ble.BleNotifyParse;
import com.example.kevin.health.ble.Config;
import com.example.kevin.health.ble.DeviceListActivity;
import com.example.kevin.health.ble.cmd_getNowData;
import com.example.kevin.health.ble.cmd_getPower;
import com.example.kevin.health.databinding.FragmentSportBinding;


/**
 * Created by hyx on 2017/2/6.
 */

public class SportFragment extends BaseFragment implements SportContract.View {



    private SportContract.Presenter presenter;

    private SeekBar mSeekBar;
    private StepView stepView;
    private EditText mEtMax;

    private TextView tvDeviceStatus;
    private LinearLayout layoutHeartRate;
    private LinearLayout layouSportReport;

    private  int Step = 0;

    private Button btnDeviceStatus;
    private Config config;
    private BluetoothAdapter mBluetoothAdapter;

    // 服务
    private UartService mUartService = null;
    private Handler handler =new Handler();


    public static SportFragment newInstance() {
        SportFragment fragment = new SportFragment();
        return fragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentSportBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sport, container, false);
        stepView = binding.svStep;
        mSeekBar = binding.seekBar;
        mEtMax = binding.etMax;
        btnDeviceStatus = binding.btnDeviceStatus;
        tvDeviceStatus = binding.tvDeviceStatus;
        layoutHeartRate=binding.layoutHeartRate;
        layouSportReport= binding.layoutSportReport;
        return binding.getRoot();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        presenter = new SportPresenter();
        presenter.start();
        showStep();


    }
        @Override
        public void showStep () {
            stepView.setMaxProgress(8000);//设置每日目标步数
            stepView.setProgress(8888);//每日步数目标
            mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    stepView.setProgress(progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            mEtMax.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String max = s.toString();

                    int maxProgress = 0;
                    try {
                        maxProgress = Integer.parseInt(max);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    stepView.setMaxProgress(maxProgress);

                }
            });

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ;
                            stepView.stopAnimator(Step); //手环传过来的步数

                        }
                    });
                }
            }).start();

        }

    @Override
    public void onDetach() {
        super.onDetach();
        presenter.detachView();
    }










    private void setTvDeviceStatus(String status) {
        tvDeviceStatus.setText(status);
    }

    private void setStep(int Step) {
        stepView.stopAnimator(Step);
    }

    private void setDistance(int Step) {
        stepView.setFormat(Step);
    }
    private void setCal(int cal) {
        stepView.setCarol(cal);
    }

    @Override
    public void onResume() {
        super.onResume();
        showStep();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

    }



}