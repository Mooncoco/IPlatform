package com.izhangxin.platform.miui;

import java.util.UUID;

import com.xiaomi.gamecenter.sdk.MiCommplatform;
import com.xiaomi.gamecenter.sdk.MiErrorCode;
import com.xiaomi.gamecenter.sdk.OnLoginProcessListener;
import com.xiaomi.gamecenter.sdk.OnPayProcessListener;
import com.xiaomi.gamecenter.sdk.entry.MiBuyInfoOffline;
import com.xiaomi.gamecenter.sdk.entry.MiBuyInfoOnline;

import android.app.Activity;
import android.content.Context;
import android.util.Log;


public class AndroidPlatformInterface
{
	private static Context mContext;

	public static void setContext(Context context) {mContext = context;}

	public static boolean MIUIInit()
	{
		Log.i("AndroidPlatfornInterface", "MIUIInit");

		return true;
	}
	
	public static void MIUILogin()
	{
		Log.i("AndroidPlatfornInterface", "MIUILogin");
		
		MiCommplatform.getInstance().miLogin((Activity)mContext, new OnLoginProcessListener() {

			@Override
			public void finishLoginProcess(int code) {
				// TODO Auto-generated method stub
				switch( code ) {

				case MiErrorCode.MI_XIAOMI_GAMECENTER_SUCCESS: // 登陆成功
				//获取用户的登陆后的 UID(即用户唯一标识)
					String uid = MiCommplatform.getInstance().getLoginUid();
				//获取用户的登陆的 Session(请参考 4.2.3.3 流程校验 Session 有效性)
					String session = MiCommplatform.getInstance().getSessionId();//若没有登录返回 null
				//请开发者完成将 uid 和 session 提交给开发者自己服务器进行 session 验证
					
//					String nickname = MiCommplatform.getInstance().getLoginNickName();
					String nickname = "小米ID"+uid;
					
					MIUILoginCallBack(uid, session, nickname);
					
					break;
				case MiErrorCode.MI_XIAOMI_GAMECENTER_ERROR_LOGIN_FAIL:
				// 登陆失败
					MIUILoginFailCallBack(code);
					break;
				case MiErrorCode.MI_XIAOMI_GAMECENTER_ERROR_CANCEL:
				// 取消登录
					MIUILoginCancelCallBack();
					break; 
				default:
				// 登录失败 break;
				}
			}
			
		});
	}
	
	public static native void MIUILoginCallBack(String uid, String session, String nickname);
	
	public static native void MIUILoginFailCallBack(int error_code);
	
	public static native void MIUILoginCancelCallBack();
	
	public static void MIUIPay(String order, String productID)
	{
		MiBuyInfoOffline offline = new MiBuyInfoOffline();
		offline.setCpOrderId(order);
		offline.setProductCode(productID);
		offline.setCount(1);

		MiCommplatform.getInstance().miUniPayOffline((Activity)mContext, offline, new OnPayProcessListener() {
			
			@Override
			public void finishPayProcess(int arg0) {
				// TODO Auto-generated method stub
				if ( arg0 == MiErrorCode.MI_XIAOMI_GAMECENTER_SUCCESS )// 成功
				{
					MIUIPaySuccessCallBack();
				}
				else if ( arg0 == MiErrorCode.MI_XIAOMI_GAMECENTER_ERROR_CANCEL || arg0 == MiErrorCode.MI_XIAOMI_GAMECENTER_ERROR_PAY_CANCEL )// 取消
				{
					MIUIPayCancelCallBack();
				}
				else if ( arg0 == MiErrorCode.MI_XIAOMI_GAMECENTER_ERROR_PAY_FAILURE )// 失败
				{
					MIUIPayFailCallBack();
				}
			}
		});
	}

	public native static void MIUIPaySuccessCallBack();
	
	public native static void MIUIPayCancelCallBack();
	
	public native static void MIUIPayFailCallBack();

}