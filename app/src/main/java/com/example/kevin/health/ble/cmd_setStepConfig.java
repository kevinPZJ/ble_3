package com.example.kevin.health.ble;

import android.util.Log;

public class cmd_setStepConfig extends BaseBleMessage {


	public static byte mTheCmd = 0x04;
	
	public byte[] dealBleResponse(byte[] notifyData, int dataLen) {

		if (notifyData.length==0){
			return null;
		}
		int data = (int)notifyData[0];

		for (int i = 0; i < dataLen; i++) {// 输出结果

			Log.e("", "back_PhoneRemind() data["+i+"] = "+notifyData[i]);
		}
		return null;
	}
	//6820  0800 00 00 00 00 55 72 6e bf 8416
	// 6820 0400 55 72 6f ac 6e16
	public byte[] SetStep() {
		byte[] data = new byte[4];
		data[0] = (byte)0xb4;
		data[1] = (byte)0x46;
		data[2] = (byte)0x00;
		data[3] = (byte)0x16;


		return setMessageByteData(mTheCmd, data, data.length);
	}




}
