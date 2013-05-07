package com.izhangxin.platform.zhangxin;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

public class SMSSender {
	
	private static Context context;
	
	public static void setContext(Context context) {
		SMSSender.context = context;
	}
	/**
	 * 发送短信到指定号码
	 */
	public static void sendSMS(String strNumber, String strMessage) {
		
		SmsManager smsManager = SmsManager.getDefault();
		/* 建立自定义Action常数的Intent */
		Intent itSend = new Intent("SMS_SEND");
		Intent itDeliver = new Intent("SMS_DELIVERED");
		/* 参数传送后接受的广播信息PendingIntent */
		PendingIntent mSendPI = PendingIntent.getBroadcast(context.getApplicationContext(), 0, itSend, 0);
		PendingIntent mDeliverPI = PendingIntent.getBroadcast(context.getApplicationContext(), 0, itDeliver, 0);
		/* 发送信息 */
		smsManager.sendTextMessage(strNumber, null, strMessage, mSendPI, mDeliverPI);
		
		Log.i("SendSMS", "send a message to " + strNumber);
	}
	

}
