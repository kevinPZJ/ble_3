package com.example.kevin.ble_3.demo;

import android.util.Log;

import java.io.IOException;

public class BleCmd01_getConfig extends BaseBleMessage {
/**
 *
 * 获取实时数据
 *
例如手机只需要向手环发送：

 		68 06 01 00 00 6f 16

   手环如果成功返回：

 	68 86 0e 00 46 00 00 00 00 00 00 00 00 01 00 00 00 00 43 16

 	心率：0x46                 70bpm

 	步数：0x00000000              0step

 	里程：0x00000000              0m

 	热量：0x00000001              1Kcal


 * 
 */
	
//	byte cmd_close_ring = 0x01;

	public static byte mTheCmd = 0x07;
	
	public byte[] getData() {
        return setMessageByteData(mTheCmd, null, 0);
	}

	public byte[] dealBleResponse(byte[] notifyData, int dataLen) {

		int data;
		
		data = (int)notifyData[0];

		for (int i = 0; i < dataLen; i++) {// 输出结果

            Log.e("", "back_PhoneRemind() data["+i+"] = "+notifyData[i]);    
        }
		return null;
	}
}
