package com.example.kevin.health.ble;

import android.util.Log;

public class cmd_set_Sit extends BaseBleMessage {

	public static byte mTheCmd = 0x14;

	private  int data0;
	private  int data1;
	private  int data2;
	private  int data3;
	private  int data4;
	private  int data5;
	private  int data6;
	private  int data7;



	
	public byte[] dealBleResponse(byte[] notifyData, int dataLen) {
		int data;

		data = (int)notifyData[0];

		for (int i = 0; i < dataLen; i++) {// 输出结果

			Log.e("", "back_PhoneRemind() data["+i+"] = "+notifyData[i]);
		}
		return null;
	}
	
	
	//6820  0800 00 00 00 00 55 72 6e bf 8416
	// 6820 0400 55 72 6f ac 6e16
	
	public byte[] SetSit() {
		byte[] data = new byte[8];
		data[0] = (byte)0x01;
		data[1] = (byte)0x00;
		data[2] = (byte)0x01;
		data[3] = (byte)0x0a;
		data[4] = (byte)0x0e;
		data[5] = (byte)0x0a;
		data[6] = (byte)0x10;
		data[7] = (byte)0x1e;
    	return setMessageByteData(mTheCmd, data, data.length);
	}




}
