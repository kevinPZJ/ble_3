package com.example.kevin.health.ble;

import android.util.Log;

public class cmd_getPower extends BaseBleMessage {
/**
 * 
例如手机只需要向手环发送：
		 68  03  0000 6b  16
   手环如果成功返回：
		 68  83  0100 00 6c  16
 * 
 */
	
//	byte cmd_close_ring = 0x01;

	public static byte mTheCmd = 0x03;
	
	public byte[] getPower() {
        return setMessageByteData(mTheCmd, null, 0);
	}

	public byte[] dealBleResponse(byte[] notifyData, int dataLen) {

		int power;
		
		power = (int)notifyData[0];

		for (int i = 0; i < dataLen; i++) {// 输出结果   
            Log.e("", "back_PhoneRemind() data["+i+"] = "+notifyData[i]);    
        }
		return null;
	}
}
