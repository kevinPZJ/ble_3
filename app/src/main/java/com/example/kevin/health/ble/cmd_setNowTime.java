package com.example.kevin.health.ble;

import java.util.Calendar;
import java.util.TimeZone;

public class cmd_setNowTime extends BaseBleMessage {


/**
 * 
手环发送：68  A0  0000  08  16	；
手机回复的数据域的时间应该是4个字节，这4个字节是当前时间转换成秒的一个数字的表示（低字节在前，高字节在后），
例如当前时间是2014-09-19 16:28:05.834，转换成毫秒是1411115285834，转换成秒则是1411115285，
转换成字节则是54  1B  E9  15，则手机回复给手环的信息应该是：68  20  0400  15E91B54  F9  16
 * 
 */
	public static byte mTheCmd = 0x20;
	
	public byte[] dealBleResponse(byte[] notifyData, int dataLen) {
//	int power;
//
//		power = (int)notifyData[0];
//
//		for (int i = 0; i < dataLen; i++) {// 输出结果
//			Log.e("", "back_PhoneRemind() data["+i+"] = "+notifyData[i]);
//		}
		return null;
	}

	
	
	//6820  0800 00 00 00 00 55 72 6e bf 8416
	// 6820 0400 55 72 6f ac 6e16
	
	public byte[] syncCurrentTime() {
		long time_sec, time_msec;
		TimeZone timeZone;
		byte[] time = new byte[4];
		Calendar now = Calendar.getInstance();
		
		timeZone = now.getTimeZone();
    	time_msec = System.currentTimeMillis() + timeZone.getRawOffset();
    	time_sec = time_msec / 1000;
    	
    	time[0] = (byte)(time_sec & 0xff);
    	time[1] = (byte)((time_sec >> 8) & 0xff);
    	time[2] = (byte)((time_sec >> 16) & 0xff);
    	time[3] = (byte)((time_sec >> 24) & 0xff);
		
    	return setMessageByteData(mTheCmd, time, time.length);
	}
	
}
