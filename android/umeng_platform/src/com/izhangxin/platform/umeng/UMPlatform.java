package com.izhangxin.platform.umeng;

import com.umeng.analytics.MobclickAgent;

import android.content.Context;

public class UMPlatform {

	private static Context mContext;
	
	public static void setContext(Context con) { mContext = con; }
	
	public static void onResume() {
		MobclickAgent.onResume(mContext);
	}
	
	public static void onPause() {
		MobclickAgent.onPause(mContext);
	}
	
}
