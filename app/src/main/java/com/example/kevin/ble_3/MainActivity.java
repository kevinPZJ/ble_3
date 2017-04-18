package com.example.kevin.ble_3;

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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kevin.ble_3.demo.BaseBleMessage;
import com.example.kevin.ble_3.demo.BleCmd011_upLoad;
import com.example.kevin.ble_3.demo.BleCmd012_setStep;
import com.example.kevin.ble_3.demo.BleCmd01_getConfig;
import com.example.kevin.ble_3.demo.BleCmd03_getPower;
import com.example.kevin.ble_3.demo.BleCmd06_getData;
import com.example.kevin.ble_3.demo.BleCmd07_getAlert;
import com.example.kevin.ble_3.demo.BleCmd08_getSit;
import com.example.kevin.ble_3.demo.BleCmd20_syncTime;
import com.example.kevin.ble_3.demo.BleNotifyParse;
import com.example.kevin.ble_3.demo.Config;
import com.example.kevin.ble_3.demo.DeviceListActivity;
import com.example.kevin.ble_3.utils.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;




public class MainActivity extends AppCompatActivity {



//    private static  final  String PATH =
    private static final String FILE_NAME = "/log/";
    private static final String FILE_NAME_SUFFIX = ".txt";




    private static final int REQUEST_SELECT_DEVICE = 101;
    private static final int REQUEST_ENABLE_BT = 102;
    private static final int UART_PROFILE_READY = 10;
    public static final String TAG = "RFUART";
    private static final int UART_PROFILE_CONNECTED = 20;
    private static final int UART_PROFILE_DISCONNECTED = 21;
    private static final int STATE_OFF = 10;
    private static final int GET_POWER = 100;

    private StringBuffer sb;

    private int mState = UART_PROFILE_DISCONNECTED;


    private BluetoothDevice mDevice = null;


    BleNotifyParse bleNotify = new BleNotifyParse();

    private byte[] tx_data = new byte[512];
    private int tx_data_len = 0;
    private int tx_data_front = 0;
    private int tx_data_rear = 0;


    final int intf_none = 0;
    final int intf_ble_uart = 1;
    int intf = intf_ble_uart;


    private TextView tvDeviceName;
    private TextView tvDeviceState;
    private Button btnScan;

    Config config;

    private BluetoothAdapter mBluetoothAdapter;

    private UartService mUartService = null;

    private String response;
    private String FiterStr;

    int time_flag = 0;
    private Handler handler;


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

//
//    public static MainActivity hrDK;
//    public static Activity mActivity;

    private TextView tvPower;
    private Button btnGetPower;
    private boolean bStartHRTest = false;
    private Button btnGetHeartRate;
    private Button btnGetStep;
    private Button btnSetTime;
    private TextView tvHeartRate;
    private TextView tvStep;
    private TextView tvDistance;
    private Button btnOpenHeartRate;
    private TextView tvHot;
    private Button btnGetBanben;


    private  String    srt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (Build.VERSION.SDK_INT>=23)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.LOCATION_HARDWARE,Manifest.permission.ACCESS_FINE_LOCATION}, 10);   }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 11);   }
        }

        config = new Config(MainActivity.this);
        initView();
        initBle();
        initService();
        initOnClickEvent();
        if (config.isValid() == true) {
            bStartHRTest = true;
            mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(config.getAddr());
            tvDeviceName.setText(config.getName());
            tvDeviceState.setText("Connecting");
        }

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {

                    case GET_POWER:

                        break;


                }
            }
        };

        sb = new StringBuffer();

    }

    private void initOnClickEvent() {
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bStartHRTest = !bStartHRTest;
                if (bStartHRTest) {
                    Intent newIntent = new Intent(MainActivity.this, DeviceListActivity.class);
                    startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
                } else {
                    // Disconnect button pressed

                    btnScan.setText("Start");
                    config.clear_config();
                    //mTextView2.setText("");
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

        tvDistance.setText(Utils.getUTCTimeStr());


        btnGetStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTx_data(new BleCmd06_getData().onHR());
            }
        });

        btnOpenHeartRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTx_data(new BleCmd06_getData().getAllDate());
            }
        });

        btnSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Log.e("time", Utils.getUTCTimeStr());

                BleCmd20_syncTime bleCmd20_syncTime = new BleCmd20_syncTime();
                setTx_data(bleCmd20_syncTime.syncCurrentTime());

            }
        });
        btnGetPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setTx_data(new BleCmd03_getPower().getPower());


            }
        });


        btnGetBanben.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                setTx_data(new BleCmd01_getConfig().getData());

//                setTx_data(new BleCmd07_getAlert().Alert());

//                setTx_data(new BleCmd08_getSit().GetSit());

//                setTx_data(new BleCmd011_upLoad().SetupLoad());

//                  setTx_data(new BleCmd012_setStep().SetStep());


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
        tvDeviceName = (TextView) findViewById(R.id.tv_device_name);
        tvDeviceState = (TextView) findViewById(R.id.tv_device_State);
        btnScan = (Button) findViewById(R.id.btn_scan);
        tvPower = (TextView) findViewById(R.id.tv_power);
        btnGetPower = (Button) findViewById(R.id.btn_get_power);
        btnGetHeartRate = (Button) findViewById(R.id.btn_get_heart_rate);
        btnGetStep = (Button) findViewById(R.id.btn_get_step);
        btnSetTime = (Button) findViewById(R.id.btn_set_time);
        tvHeartRate = (TextView) findViewById(R.id.tv_heart_rate);
        tvStep = (TextView) findViewById(R.id.tv_step);
        tvDistance = (TextView) findViewById(R.id.tv_distance);
        btnOpenHeartRate = (Button) findViewById(R.id.btn_open_heart_rate);
        tvHot = (TextView) findViewById(R.id.tv_hot);
        btnGetBanben = (Button) findViewById(R.id.btn_get_banben);
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
                Log.d(TAG, "onServiceConnected mService= " + mUartService)
                ;
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
            // *********************//
            if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d(TAG, "UART_CONNECT_MSG");
                        tvDeviceName.setText(mDevice.getName());
                        tvDeviceState.setText("Connected");
                        mState = UART_PROFILE_CONNECTED;
                        if (config.isValid() == false) {
                            config.save_config(mDevice.getName(), mDevice.getAddress());
                        }
                    }
                });
            }

            // *********************//
            if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d(TAG, "UART_DISCONNECT_MSG");
                        tvDeviceState.setText("Disconnect");
                        mState = UART_PROFILE_DISCONNECTED;
                        mUartService.close();
                        if (config.isValid() == true) {

                            tvDeviceState.setText("Connecting");

                            mUartService.connect(mDevice.getAddress());
                        }


                    }
                });
            }

            // *********************//
            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
                mUartService.enableTXNotification();
                try {
                    Thread.currentThread().sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            // *********************//
            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {


                byte[] datapackage = intent.getByteArrayExtra(UartService.EXTRA_DATA);

                bleNotify.doParse(MainActivity.this, datapackage);

                 srt= bleNotify.getResponse();

                        Log.e("66666666666666666",  srt );

//                sb.append(BaseBleMessage.bytesToHexString(datapackage));
//
//                response = sb.toString().toUpperCase();
//
//
//                String data = response.substring(2, response.length() - 2);
//
//                String cmd_head = data.substring(0, 2);
//
//                Log.e("response", response + "");


                if (srt!=null) {
                    switch (srt.substring(2, 4)) {
                        case "83":
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String power = srt.substring(8, 10);
                                    BigInteger srch = new BigInteger(power, 16);
                                    tvPower.setText(srch.toString());

                                }
                            });
                            break;

                        case "86":
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    String heart = srt.substring(10, 12);
                                    BigInteger heartforHex = new BigInteger(heart, 16);
                                    tvHeartRate.setText(heartforHex.toString());


                                    String steps_01 = srt.substring(10, 14);
                                    BigInteger stepforHex_01 = new BigInteger(steps_01, 16);
                                    int step_01 = Integer.parseInt(stepforHex_01.toString());
                                    String steps_02 = srt.substring(14, 18);

                                    BigInteger stepforHex_02 = new BigInteger(steps_02, 16);
                                    int step_02 = Integer.parseInt(stepforHex_02.toString());


                                    tvStep.setText((step_01 + step_02) + "");


                                    String dis_01 = srt.substring(18, 22);
                                    BigInteger disforHex_01 = new BigInteger(dis_01, 16);
                                    int num_dis_01 = Integer.parseInt(disforHex_01.toString());

                                    String dis_02 = srt.substring(22, 26);
                                    BigInteger disforHex_02 = new BigInteger(dis_02, 16);
                                    int num_dis_02 = Integer.parseInt(disforHex_02.toString());

                                    tvDistance.setText((num_dis_01 + num_dis_02) + "米");


                                    String cal_01 = srt.substring(26, 30);
                                    BigInteger calorHex_01 = new BigInteger(cal_01, 16);
                                    int num_cal_01 = Integer.parseInt(calorHex_01.toString());

                                    String cal_02 = srt.substring(30, 34);
                                    BigInteger calforHex_02 = new BigInteger(cal_02, 16);
                                    int num_cal_02 = Integer.parseInt(calforHex_02.toString());

                                    tvHot.setText((num_cal_01 + num_cal_02) + "大卡");


                                }
                            });
//                  sb.delete(0, sb.length());
                            break;
                        default:
                            break;

                    }


                    if ("68".equals(srt.substring(0, 2)) && "16".equals(srt.substring(srt.length() - 2, srt.length()))) {

                        String time = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.CHINA)
                                .format(new Date(System.currentTimeMillis()));

                        File outputImage = new File(getExternalCacheDir(), srt.substring(2, 4) + "-" + time + ".txt");
                        FileOutputStream fos = null;
                        BufferedWriter writer = null;
                        try {
                            if (outputImage.exists()) {
                                outputImage.delete();
                            }
                            outputImage.createNewFile();
                            fos = new FileOutputStream(outputImage);
                            fos.write(srt.toString().getBytes());

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
                    tvDeviceState.setText("Connecting");
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

                    tvDeviceName.setText(mDevice.getName());
                    tvDeviceState.setText("Connecting");

                    if (intf == intf_ble_uart)
                        mUartService.connect(deviceAddress);
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


}


