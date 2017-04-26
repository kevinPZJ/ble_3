package com.example.kevin.health;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kevin.health.Ui.step.StepView;
import com.example.kevin.health.ble.BleNotifyParse;
import com.example.kevin.health.ble.Config;
import com.example.kevin.health.ble.DeviceListActivity;
import com.example.kevin.health.ble.cmd_getNowData;
import com.example.kevin.health.databinding.ActivityBleBinding;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import rx.Subscription;
import rx.functions.Action1;

import rx.schedulers.Schedulers;

import static rx.Observable.interval;



public class BleActivity extends AppCompatActivity {

    private static final int REQUEST_SELECT_DEVICE = 101;
    private static final int REQUEST_ENABLE_BT = 102;
    private static final int UART_PROFILE_READY = 10;
    public static final String TAG = "RFUART";
    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int UART_PROFILE_DISCONNECTED = 21;
    private static final int STATE_OFF = 10;
    private static final int GET_POWER = 100;

    private int mState = UART_PROFILE_DISCONNECTED;


    private BluetoothDevice mDevice = null;
    private BleNotifyParse bleNotify = new BleNotifyParse();

    private byte[] tx_data = new byte[512];
    private int tx_data_len = 0;
    private int tx_data_front = 0;
    private int tx_data_rear = 0;

    final int intf_none = 0;
    final int intf_ble_uart = 1;
    int intf = intf_ble_uart;

    private LinearLayout layoutHeartRate;
    private LinearLayout layoutHeartRateChart;
    private LineChartView heartChart;
    private ImageView ivHeartArrow;

    private TextView tvDeviceStatus;
    private Button btnDeviceStatus;
    private StepView stepView;


    private Config config;
    private BluetoothAdapter mBluetoothAdapter;
    private UartService mUartService = null;


    int time_flag = 0;
    private Handler handler = new Handler();


    private Runnable runnable = new Runnable() {
        public void run() {
            if (tx_data_len > 0) {
                int len;
                if (tx_data_len > 20) {
                    len = 20;
                } else {
                    len = tx_data_len;
                }

                byte[] send_buf = new byte[len];
                for (int i = 0; i < len; i++) {
                    send_buf[i] = tx_data[tx_data_rear];
                    tx_data_rear = (tx_data_rear + 1) % 512;

                }

                if (mUartService != null) {
                    mUartService.writeRXCharacteristic(send_buf);
                }
                tx_data_len = tx_data_len - len;
            }

            if (tx_data_len > 0) {
                handler.postDelayed(this, 200);
            } else {
                time_flag = 0;
            }
        }
    };


    private boolean bStartHRTest = false;
    private boolean stopSendCmd_GetData = false;
    private boolean isShowHeart;


    private String BLe_response = "00000000000000000000000000000000000000000000000000000000000000000000000000000000";

    private int step = 0;
    private int distance;
    private int cal;
    private float heartRate = 0 ;

    private Subscription getDataSub;
    private Subscription getHeartSub;


    private LineChartData lineChartData;
    private List<Line> linesList;
    private List<PointValue> pointValueList;
    private int position = 0;
    private Axis axisY, axisX;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.LOCATION_HARDWARE, Manifest.permission.ACCESS_FINE_LOCATION}, 10);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 11);
            }
        }
        config = new Config(BleActivity.this);
        initView();
        initBle();
        initService();
        initOnClickEvent();
        if (config.isValid() == true) {
            bStartHRTest = true;
            mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(config.getAddr());
        }

    }

    private void initOnClickEvent() {
        btnDeviceStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bStartHRTest == false) {

                    Intent newIntent = new Intent(BleActivity.this, DeviceListActivity.class);
                    startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
                } else {

                    config.clear_config();

                    bStartHRTest = false;
                }
                if (intf == intf_ble_uart) {
                    if (bStartHRTest) {
                        // Connect button pressed, open DeviceListActivity
                        // class, with popup windows that scan for devices
                        try {
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        // Disconnect button pressed
                        try {
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (mDevice != null) {
                            mUartService.disconnect();
                        }

                    }

                }
            }

        });
    }

    private void initBle() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            return;
        }
        if (config.isValid() == true) {
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
            }
        }

    }

    private void initView() {

        ActivityBleBinding binding = DataBindingUtil.setContentView(BleActivity.this, R.layout.activity_ble);
        btnDeviceStatus = binding.btnDeviceStatus;
        tvDeviceStatus = binding.tvDeviceStatus;
        layoutHeartRate = binding.layoutHeartRate;
        layoutHeartRateChart=binding.layoutHeartRateChart;
        ivHeartArrow=binding.ivHeartArrow;
        heartChart=binding.heartChart;
        stepView = binding.Step;

        stepView.setMaxProgress(10000);//设置每日目标步数
        stepView.setProgress(step);//每日步数
        stepView.setCarol(cal);
        stepView.setFormat(distance);
//        setStep();

        layoutHeartRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowHeart=!isShowHeart;
               if (isShowHeart) {
                   layoutHeartRateChart.setVisibility(View.VISIBLE);
                   ivHeartArrow.setImageResource(R.mipmap.arrow_down);
                   showHeart();
               }else {
                   layoutHeartRateChart.setVisibility(View.GONE);
                   ivHeartArrow.setImageResource(R.mipmap.arrow_right);
                   getHeartSub.unsubscribe();
                   pointValueList.clear();
                   linesList.clear();
                   position=0;

               }
            }
        });}

    private void getData() {
        if (mDevice != null) {
            if (mDevice.getAddress() != null & !"".equals(mDevice.getAddress())) {

                getDataSub = interval(1, TimeUnit.SECONDS, Schedulers.newThread()).subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        setTx_data(new cmd_getNowData().getAllData());
                    }
                });

            }
        }
    }


    private void initService() {

        Intent bindIntent = new Intent(this, UartService.class);
        bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT);
        return intentFilter;
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                                       IBinder rawBinder) {
            if (intf == intf_ble_uart) {
                mUartService = ((UartService.LocalBinder) rawBinder).getService();
                Log.d(TAG, "onServiceConnected mService= " + mUartService);
                if (!mUartService.initialize()) {
                    Log.e(TAG, "Unable to initialize Bluetooth");
                    finish();
                } else {
                    if (config.isValid() == true) {
                        if (mBluetoothAdapter.isEnabled()) {
                            //mTextView2.setText("Connecting");
                            mUartService.connect(config.getAddr());

                        }
                    }
                }
            }
        }

        public void onServiceDisconnected(ComponentName classname) {
            // // mService.disconnect(mDevice);
            if (intf == intf_ble_uart) {
                mUartService = null;
            }
        }
    };

    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {


        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            final Intent mIntent = intent;

            if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d(TAG, "UART_CONNECT_MSG");

                        setTvDeviceStatus("Connected");
                        setBtnDeviceStatus("断开连接");
                        mState = UART_PROFILE_CONNECTED;

                        if (config.isValid() == false) {
                            config.save_config(mDevice.getName(), mDevice.getAddress());
                        }
                    }
                });
            }


            if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {

                        Log.d(TAG, "UART_DISCONNECT_MSG");
                        setTvDeviceStatus("Disconnect");
                        setBtnDeviceStatus("未连接");

                        mState = UART_PROFILE_DISCONNECTED;
                        mUartService.close();
                        if (config.isValid() == true) {

                            setTvDeviceStatus("Connecting");
                            mUartService.connect(mDevice.getAddress());
                        }


                    }
                });
            }

            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
                mUartService.enableTXNotification();
                try {
                    Thread.currentThread().sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {

                byte[] datapackage = intent.getByteArrayExtra(UartService.EXTRA_DATA);

                bleNotify.doParse(BleActivity.this, datapackage);

                BLe_response = bleNotify.getResponse();

                Log.e("66666666666666666", BLe_response + "");


                if (BLe_response != null) {
                    switch (BLe_response.substring(2, 4)) {
                        case "83":
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String power = BLe_response.substring(8, 10);
                                    BigInteger srch = new BigInteger(power, 16);
                                }
                            });
                            break;

                        case "86":
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    String heart = BLe_response.substring(10, 12);
                                    BigInteger heartforHex = new BigInteger(heart, 16);

                                    heartRate = Float.valueOf(heartforHex.toString());

                                    String steps_01 = BLe_response.substring(12, 14);
                                    BigInteger stepforHex_01 = new BigInteger(steps_01, 16);
                                    int step_01 = Integer.parseInt(stepforHex_01.toString());
                                    String steps_02 = BLe_response.substring(14, 18);

                                    BigInteger stepforHex_02 = new BigInteger(steps_02, 16);
                                    int step_02 = Integer.parseInt(stepforHex_02.toString());

//                                    tvStep.setText((step_01 + step_02) + "");
                                    step = step_01 + step_02;

                                    String dis_01 = BLe_response.substring(18, 22);
                                    BigInteger disforHex_01 = new BigInteger(dis_01, 16);
                                    int num_dis_01 = Integer.parseInt(disforHex_01.toString());

                                    String dis_02 = BLe_response.substring(22, 26);
                                    BigInteger disforHex_02 = new BigInteger(dis_02, 16);
                                    int num_dis_02 = Integer.parseInt(disforHex_02.toString());

//                                    tvDistance.setText((num_dis_01 + num_dis_02) + "米");
                                    distance = num_dis_01 + num_dis_02;

                                    String cal_01 = BLe_response.substring(26, 30);
                                    BigInteger calorHex_01 = new BigInteger(cal_01, 16);
                                    int num_cal_01 = Integer.parseInt(calorHex_01.toString());

                                    String cal_02 = BLe_response.substring(30, 34);
                                    BigInteger calforHex_02 = new BigInteger(cal_02, 16);
                                    int num_cal_02 = Integer.parseInt(calforHex_02.toString());

//                                    tvHot.setText((num_cal_01 + num_cal_02) + "大卡");

                                    cal = (num_cal_01 + num_cal_02);


                                    stepView.setFormat(distance);
                                    stepView.setCarol(cal);


                                    stepView.stopAnimator(step);

                                    stopSendCmd_GetData = true;
                                }
                            });

                            break;
                        default:
                            break;

                    }


                    if ("68".equals(BLe_response.substring(0, 2)) && "16".equals(BLe_response.substring(BLe_response.length() - 2, BLe_response.length()))) {

                        String time = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.CHINA)
                                .format(new Date(System.currentTimeMillis()));

                        File outputImage = new File(getExternalCacheDir(), BLe_response.substring(2, 4) + "-" + time + ".txt");
                        FileOutputStream fos = null;
                        BufferedWriter writer = null;
                        try {
                            if (outputImage.exists()) {
                                outputImage.delete();
                            }
                            outputImage.createNewFile();
                            fos = new FileOutputStream(outputImage);
                            fos.write(BLe_response.toString().getBytes());

                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            if (writer != null) {
                                try {
                                    writer.close();
//

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }

                }

            } else {
                return;
            }
            if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT)) {
                //showMessage("Device doesn't support UART. Disconnecting");
                mUartService.disconnect();
                try {
                    Thread.currentThread().sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mUartService.close();
                if (config.isValid() == true) {
//                    tvDeviceState.setText("Connecting");
                    setTvDeviceStatus("Connecting");
                    mUartService.connect(mDevice.getAddress());
                }
            }

        }
    };

    public void setTx_data(byte[] tx_data) {
        if (tx_data == null) {
            return;
        }
        int len = tx_data.length;
        if (mUartService == null) {
            return;
        }
        if (mUartService.isConnected() != true) {
            return;
        }
        for (int i = 0; i < len; i++) {
            if (tx_data_len >= 512) {
                tx_data_rear = (tx_data_rear + 1) % 512;
                tx_data_len--;
            }
            this.tx_data[tx_data_front] = tx_data[i];
            tx_data_front = (tx_data_front + 1) % 512;
            tx_data_len++;
        }
        if (time_flag == 0) {
            handler.postDelayed(runnable, 200);
            time_flag = 1;
        }
    }


    @Override
    public void onResume() {
        super.onResume();

//        initService();
//        getData();

        if (bStartHRTest) {
            setTvDeviceStatus("Connected");
        } else {
            setTvDeviceStatus("DisConnected");
        }


        if (intf == intf_ble_uart) {
            if (mBluetoothAdapter != null) {
                if (!mBluetoothAdapter.isEnabled()) {
                    Log.i(TAG, "onResume - BT not enabled yet");
                    Intent enableIntent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                }
            }
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_SELECT_DEVICE:
                // When the DeviceListActivity return, with the selected device
                // address
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                    mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
//                    tvDeviceName.setText(mDevice.getName());
                    setTvDeviceStatus("Connecting");
//                    tvDeviceStatus.setText("Connecting");

                    if (intf == intf_ble_uart) mUartService.connect(deviceAddress);
                    bStartHRTest = true;
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "Bluetooth has turned on ",
                            Toast.LENGTH_SHORT).show();

                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, "Problem in BT Turning ON ",
                            Toast.LENGTH_SHORT).show();
                    //finish();
                }
                break;
            default:
                Log.e(TAG, "wrong request code");
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");

        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(
                    UARTStatusChangeReceiver);
        } catch (Exception ignore) {
            Log.e(TAG, ignore.toString());
        }

        try {
            unbindService(mServiceConnection);
        } catch (Exception ignore) {
            Log.e(TAG, ignore.toString());
        }
        if (mUartService != null) {
            mUartService.stopSelf();
            mUartService = null;
        }
    }






    private void  showHeart(){

        pointValueList = new ArrayList<>();
        linesList = new ArrayList<>();
        //初始化坐标轴
        axisY = new Axis();
        //添加坐标轴的名称
        axisY.setLineColor(Color.parseColor("#aab2bd"));
        axisY.setTextColor(Color.parseColor("#aab2bd"));
        axisX = new Axis();
        axisX.setLineColor(Color.parseColor("#aab2bd"));
        lineChartData = initDatas(null);
        heartChart.setLineChartData(lineChartData);

        Viewport port = initViewPort(0, 30);
        heartChart.setCurrentViewportWithAnimation(port);
        heartChart.setInteractive(false);
        heartChart.setScrollEnabled(true);
        heartChart.setValueTouchEnabled(true);
        heartChart.setFocusableInTouchMode(true);
        heartChart.setViewportCalculationEnabled(false);
        heartChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        heartChart.startDataAnimation();




        getHeartSub =interval(1, TimeUnit.SECONDS, Schedulers.newThread()).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {

                setTx_data(new cmd_getNowData().getAllData());

                PointValue value1 = new PointValue(position * 5,
                      heartRate);
                pointValueList.add(value1);

                float x = value1.getX();
                //根据新的点的集合画出新的线
                Line line = new Line(pointValueList);
                line.setColor(Color.RED);
                line.setShape(ValueShape.CIRCLE);
                line.setCubic(true);//曲线是否平滑，即是曲线还是折线
                line.setHasLabels(true);//曲线的数据坐标是否加上备注

                linesList.clear();
                linesList.add(line);
                lineChartData = initDatas(linesList);
                heartChart.setLineChartData(lineChartData);
                //根据点的横坐实时变幻坐标的视图范围
                Viewport port;
                if (x > 30) {
                    port = initViewPort(x - 30, x);
                } else {
                    port = initViewPort(0, 30);
                }
                heartChart.setCurrentViewport(port);//当前窗口

                Viewport maPort = initMaxViewPort(x);
                heartChart.setMaximumViewport(maPort);//最大窗口
                position++;
            }
        });



    }

    private LineChartData initDatas(List<Line> lines) {
        LineChartData data = new LineChartData(lines);
        data.setAxisYLeft(axisY);
        data.setAxisXBottom(axisX);
        return data;
    }


    /**
     * 当前显示区域
     * @param left
     * @param right
     * @return
     */
    private Viewport initViewPort(float left, float right) {
        Viewport port = new Viewport();
        port.top = 150;
        port.bottom = 0;
        port.left = left;
        port.right = right;
        return port;
    }

    /**
     * 最大显示区域
     *
     * @param right
     * @return
     */
    private Viewport initMaxViewPort(float right) {
        Viewport port = new Viewport();
        port.top = 150;
        port.bottom = 0;
        port.left = 0;
        port.right = right + 50;
        return port;
    }


    private void setTvDeviceStatus(String status) {
        tvDeviceStatus.setText(status);

    }

    private void setBtnDeviceStatus(String status) {

        btnDeviceStatus.setText(status);

    }
}










