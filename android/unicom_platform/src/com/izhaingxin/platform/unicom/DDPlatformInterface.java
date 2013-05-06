package com.izhaingxin.platform.unicom;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class DDPlatformInterface {
	private static String TAG = "unicom_platform";

	private static Context mContext = null;
	private static mServiceReceiver sendReceiver = new mServiceReceiver();

	public static void setContext(Context con) {
		mContext = con;
	}

	public static boolean DDInit() {
		Log.i(TAG, "DDInit");

		return true;
	}

	private static String ACTION_SMS_SEND = "com.izhangxin.platform.duandai.sms.send";
	private static String ACTION_SMS_DELIVERY = "com.izhangxin.platform.duandai.sms.delivery";

	// 移动
	private static String operator1_1 = "46000";
	private static String operator1_2 = "46002";
	private static String operator1_3 = "46007";
	// 联通
	private static String operator2 = "46001";
	// 电信
	private static String operator3 = "46003";

	public static void DDPurchase(String order, String serino, int price) {
		Log.e("================>", "DDPurchase order:" + order + "serino:"
				+ serino + "price:" + price);

		List<String> code_ = null;

		code_ = getCode(price, order, serino);

		if (code_ == null)
			onDDPurchaseFail("");
		else {
			// TODO Auto-generated method stub
			try {
				SmsManager smsManager = SmsManager.getDefault();

				// 创建自定义Action常数的Intent(给PendingIntent参数之用)
				Intent itSend = new Intent(ACTION_SMS_SEND);
				Intent itDeliver = new Intent(ACTION_SMS_DELIVERY);

				// sentIntent参数为传送后接受的广播信息PendingIntent
				PendingIntent mSendPI = PendingIntent.getBroadcast(mContext, 0,
						itSend, 0);

				// deliveryIntent参数为送达后接受的广播信息PendingIntent
				PendingIntent mDeliverPI = PendingIntent.getBroadcast(mContext,
						0, itDeliver, 0);

				// 发送SMS短信，注意倒数的两个PendingIntent参数
				List<String> divideContents = smsManager.divideMessage(code_
						.get(1));

				for (String text : divideContents) {
					smsManager.sendTextMessage(code_.get(0), null, text,
							mSendPI, mDeliverPI);
				}

				IntentFilter sendFilter = new IntentFilter(ACTION_SMS_SEND);
				mContext.registerReceiver(sendReceiver, sendFilter);

				/*
				 * mServiceReceiver deliveryReceiver = new mServiceReceiver();
				 * IntentFilter deliveryFilter = new
				 * IntentFilter(ACTION_SMS_DELIVERY);
				 * mContext.registerReceiver(deliveryReceiver, deliveryFilter);
				 */
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private static List<String> getCode(int price, String order, String serino) {
		// TODO Auto-generated method stub
		List<String> codes_ = new ArrayList<String>();

		String str[] = serino.split("_");

		if (str.length != 2) {
			return null;
		}

		codes_.add(str[1]);

		StringBuffer strContent = new StringBuffer(str[0]);

		int operatorId = getPhoneOperator();

		// 联通和电信----
		if (operatorId == 2 || operatorId == 3)
			strContent.append(order);

		codes_.add(strContent.toString());

		System.out.println("==================>sendip:" + str[1] + "content:"
				+ strContent.toString());

		return codes_;
	}

	/* 自定义mServiceReceiver覆盖BroadcastReceiver聆听短信状态信息 */
	public static class mServiceReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			try {
				/* android.content.BroadcastReceiver.getResultCode()方法 */
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					/* 发送短信成功，这里写需要的代码 */
					Log.i(TAG, intent.getAction() + " Activity.RESULT_OK");
					// 移动的卡是走的客户端通知流程,
					if (getPhoneOperator() == 1)
						onDDPurchaseSmSSuccess();
					else
						onDDPurchaseSuccess();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					/* 发送短信失败 */
					Log.i(TAG, "SmsManager.RESULT_ERROR_GENERIC_FAILURE");
					// break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					Log.i(TAG, "SmsManager.RESULT_ERROR_RADIO_OFF");
					// break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					Log.i(TAG, "SmsManager.RESULT_ERROR_NULL_PDU");
					// break;
					onDDPurchaseFail("");
					break;
				}
			} catch (Exception e) {
				e.getStackTrace();
			}
		}
	}

	// 返回手机号码所属运营商
	public static int getPhoneOperator() {
		try {
			TelephonyManager tm = (TelephonyManager) mContext
					.getSystemService(Context.TELEPHONY_SERVICE);

			String operator = tm.getSimOperator();
			if (operator != null) {
				if (operator.equals(operator1_1)
						|| operator.equals(operator1_2)
						|| operator.equals(operator1_3)) {
					// 中国移动
					return 1;
				} else if (operator.equals(operator2)) {
					// 中国联通
					return 2;
				} else if (operator.equals(operator3)) {
					// 中国电信
					return 3;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static native void onDDPurchaseSuccess();

	public static native void onDDPurchaseSmSSuccess();

	public static native void onDDPurchaseCancel();

	public static native void onDDPurchaseFail(String error);
}
