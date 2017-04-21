package com.example.kevin.health.ble;

import android.util.Log;

public class cmd_upLoad extends BaseBleMessage {


	public static byte mTheCmd = 0x02;
	
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
	
	public byte[] SetupLoad() {
		byte[] data = new byte[11];
		data[0] = (byte)0x01;
		data[1] = (byte)0x00;
		data[2] = (byte)0x01;
		data[3] = (byte)0x01;
		data[4] = (byte)0x01;
		data[5] = (byte)0x02;
		data[6] = (byte)0x05;
		data[7] = (byte)0x03;
		data[8] = (byte)0x01;
		data[9] = (byte)0x04;
		data[10] = (byte)0x01;

		return setMessageByteData(mTheCmd, data, data.length);
	}

	public byte[] GetLoad() {
		byte[] data = new byte[6];
		data[0] = (byte)0x00;
		data[1] = (byte)0x00;
		data[2] = (byte)0x01;
		data[3] = (byte)0x02;
		data[4] = (byte)0x03;
		data[5] = (byte)0x04;
		return setMessageByteData(mTheCmd, data, data.length);
	}



}
