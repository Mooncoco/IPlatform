package com.izhangxin.platform.zhangxin;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;


public class CommonUtil {
	
private static Context context;
	
	public static void setContext(Context context) {
		CommonUtil.context = context;
	}
	
	
	/**
	 * 计算时间间隔
	 */
	private void timeInterval(	String beginTime, String lastTime) {
		
	}
	/**
	 * 获取当前时间 
	 */
	public static String getDateTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date currentDate = new Date(System.currentTimeMillis());
		String time = formatter.format(currentDate);
		return time;
	}
	
	/**
	 * 获取手机imei、imsi
	 */
	public static String[] getPhoneInformation() {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		/* 获取设备信息IMEI和IMSI，若为空设置为“” */
		String[] strPhone = new String[2];
		strPhone[0] = (tm.getSimSerialNumber() != null) ? tm.getSimSerialNumber() : ""; // imei
		strPhone[1] = (tm.getSubscriberId() != null) ? tm.getSubscriberId() : ""; 		// imsi

		Log.i("phoneInfo", "imei:" + strPhone[0] + "imsi:" + strPhone[1]);

		return strPhone;
	}
}
