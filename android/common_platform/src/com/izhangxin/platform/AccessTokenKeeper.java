package com.izhangxin.platform;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.weibo.sdk.android.Oauth2AccessToken;
/**
 * 该类用于保存Oauth2AccessToken到sharepreference，并提供读取功能
 * @author xiaowei6@staff.sina.com.cn
 *
 */
public class AccessTokenKeeper {
	public static final String PREFERENCES_SINA_NAME = "com_weibo_sdk_android";
	public static final String PREFERENCES_TENCENT_NAME = "com_tencent_sdk_android";
	/**
	 * 保存accesstoken到SharedPreferences
	 * @param context Activity 上下文环�?
	 * @param token Oauth2AccessToken
	 */
	public static void keepAccessToken(String name,Context context, Oauth2AccessToken token) {
		SharedPreferences pref = context.getSharedPreferences(name, Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.putString("token", token.getToken());
		editor.putLong("expiresTime", token.getExpiresTime());
		editor.putString("uid", token.getmUid());
		editor.commit();
	}
	/**
	 * 清空sharepreference
	 * @param context
	 */
	public static void clear(String name,Context context){
	    SharedPreferences pref = context.getSharedPreferences(name, Context.MODE_APPEND);
	    Editor editor = pref.edit();
	    editor.clear();
	    editor.commit();
	}

	/**
	 * 从SharedPreferences读取accessstoken
	 * @param context
	 * @return Oauth2AccessToken
	 */
	public static Oauth2AccessToken readAccessToken(String name,Context context){
		Oauth2AccessToken token = new Oauth2AccessToken();
		SharedPreferences pref = context.getSharedPreferences(name, Context.MODE_APPEND);
		token.setToken(pref.getString("token", ""));
		token.setExpiresTime(pref.getLong("expiresTime", 0));
		token.setmUid(pref.getString("uid",""));
		return token;
	}
}
