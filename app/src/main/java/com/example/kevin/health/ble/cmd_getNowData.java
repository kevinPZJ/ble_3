package com.example.kevin.health.ble;

import android.util.Log;

public class cmd_getNowData extends BaseBleMessage {
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

public static byte mTheCmd = 0x06;

	public byte[] dealBleResponse(byte[] notifyData, int dataLen) {
//		syncCurrentTime();
//		return setMessageByteData(mTheCmd,onHR(),onHR().length);


		int power;

		power = (int)notifyData[0];

		for (int i = 0; i < dataLen; i++) {// 输出结果
			Log.e("", "back_PhoneRemind() data["+i+"] = "+notifyData[i]);
		}
		return null;

	}

	//6820  0800 00 00 00 00 55 72 6e bf 8416
	// 6820 0400 55 72 6f ac 6e16

	public byte[] onHR() {
		byte[] data = new byte[1];
		data[0] = 0x01;
		return setMessageByteData(mTheCmd, data, data.length);
	}

	public byte[] offHR() {
		byte[] data = new byte[1];

		data[0] = 0x02;

		return setMessageByteData(mTheCmd, data, data.length);
	}
	public byte[] getAllData() {
		byte[] data = new byte[1];

		data[0] = 0x00;

		return setMessageByteData(mTheCmd, data, data.length);
	}
}

