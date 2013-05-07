package com.izhangxin.platform.zhangxin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.util.EncodingUtils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class ZhangxinHandler {
	
	private static Context context;
	
	public static void setContext(Context context) {
		ZhangxinHandler.context = context;
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
	
	/**
	 * 判断文件是否存在(/data/data/<应用程序>目录下文件)
	 * 注意要使用绝对路径
	 */
	public static boolean exitsFile(String fileName) {
		String path = "/data/data";
		String packageName = context.getPackageName();
		File file = new File(path+"/"+packageName+"/"+fileName);
		if(!file.exists()){
			return false;
		}		
		return true;
	}
	
	/**
	 * 写入/data/data/<应用程序名>目录下文件:
	 */
	public static void writeFile(String fileName, String writestr) throws IOException {

		try {
			
			FileOutputStream fileOutputStream = context.openFileOutput(fileName,Context.MODE_PRIVATE);
			byte[] bytes = writestr.getBytes();
			fileOutputStream.write(bytes);
			fileOutputStream.close();
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取/data/data/<应用程序名>目录下的文件:
	 */
	public static String readFile(String fileName) throws IOException {
		
		String result = "";
		String lasttime = "";
		
		try {
			FileInputStream fileInputStream = context.openFileInput(fileName);
			int length = fileInputStream.available();
			byte[] buffer = new byte[length];
			fileInputStream.read(buffer);
			result = EncodingUtils.getString(buffer, "UTF-8");
			fileInputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String smsDetail[] = result.split("=");
		System.out.println(result);
		lasttime = smsDetail[1];		
		return lasttime;
	}
	
	/**
	 * 计算时间间隔
	 */
	private long timeInterval(	String startTime, String endTime, String format) {
		/* 按照传入的格式生成一个simpledateformate对象 */  
        SimpleDateFormat sd = new SimpleDateFormat(format);  
        long nd = 1000 * 24 * 60 * 60;		// 一天的毫秒数  
        long day = 0;
        long diff;
        try {  
            /*  获得两个时间的毫秒时间差异  */
            diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();  
            day = diff / nd;				 // 计算差多少天    
            // 输出结果  
            System.out.println("时间相差：" + day + "天");  
        } catch (ParseException e) {  
            e.printStackTrace();  
        }
        return day;
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
