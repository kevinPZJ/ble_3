package com.example.kevin.health.ble;

public class cmd_getAlert extends BaseBleMessage {



	public static byte mTheCmd = 0x10;
	
	public byte[] dealBleResponse(byte[] notifyData, int dataLen) {
//		syncCurrentTime();
		return null;
	}
	
	
	//6820  0800 00 00 00 00 55 72 6e bf 8416
	// 6820 0400 55 72 6f ac 6e16
	
	public byte[] Alert() {
		byte[] data = new byte[4];
		data[0] = 0x01;
		data[1] = 0x00;
		data[2] = (byte)0xa0;
		data[3] = 0x30;



    	return setMessageByteData(mTheCmd, data, data.length);
	}
	
//	public byte[] offHR() {
//		byte[] data = new byte[1];
//
//		data[0] = 0x02;
//
//    	return setMessageByteData(mTheCmd, data, data.length);
//	}

}
