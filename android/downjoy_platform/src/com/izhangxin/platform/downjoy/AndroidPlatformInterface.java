package com.izhangxin.platform.downjoy;

import com.downjoy.error.DownjoyError;
import com.downjoy.net.CallbackListener;
import com.downjoy.net.Downjoy;
import com.downjoy.util.Util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

public class AndroidPlatformInterface
{
	private static Context mContext = null;
	
	private static Downjoy downjoy = null;

	public static void setContext(Context context) {mContext = context;}

	public static boolean DJInit()
	{
		Log.i("AndroidPlatfornInterface", "DJInit");
		
		if(downjoy == null)
		{
			downjoy = new Downjoy(
					mContext.getString(R.string.merchant_id),
					mContext.getString(R.string.app_id),
					mContext.getString(R.string.server_seq_num),
					mContext.getString(R.string.app_key));
		}
		
		return true;
	}
	
	public static void DJLogin()
	{
		Log.i("AndroidPlatfornInterface", "DJLogin");
		
		((Activity)mContext).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
		        Integer serverId=1; // 服务器id,未知可填null

		        downjoy.openLoginDialog(mContext, serverId, new CallbackListener() {

		            @Override
		            public void onLoginSuccess(Bundle bundle) {
		            	
		            	Log.i("cocos2d-x debug info", "onLoginSuccess");
		            	
		            	String appid = mContext.getString(R.string.app_id);
		                String memberId=bundle.getString(Downjoy.DJ_PREFIX_STR + "mid");
		                String username=bundle.getString(Downjoy.DJ_PREFIX_STR + "username");
		                String nickname=bundle.getString(Downjoy.DJ_PREFIX_STR + "nickname");
		                String token=bundle.getString(Downjoy.DJ_PREFIX_STR + "token");

//		                Util.alert(mContext, "mid:" + memberId + "\nusername:" + username + "\nnickname:" + nickname + "\ntoken:"
//		                    + token);
		                Log.i("cocos2d-x debug info", "onLoginSuccess! mid:" + memberId + " | username:" + username + " | nickname:" + nickname + " | token:"
		                    + token);
		                
		                onDJLoginSuccess(appid, memberId, token, nickname);
		            }

		            @Override
		            public void onLoginError(DownjoyError error) {
		                int errorCode=error.getMErrorCode();
		                String errorMsg=error.getMErrorMessage();

//		                Util.alert(mContext, "onLoginError:" + errorCode + "|" + errorMsg);
		                Log.i("cocos2d-x debug info", "onLoginError:" + errorCode + "|" + errorMsg);
		                
		                if(errorCode == 100)
		                	onDJLoginCancel();
		                else
		                	onDJLoginFail(errorMsg);
		            }

		            @Override
		            public void onError(Error error) {
		                String errorMessage=error.getMessage();
//		                Util.alert(mContext, "onError:" + errorMessage);
		                Log.i("cocos2d-x debug info", "onError:" + errorMessage);
		                onDJLoginFail(errorMessage);
		            }
		        });
			}
		});
	}
	
	public static void DJLogout()
	{
		((Activity)mContext).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
		        downjoy.logout(mContext, new CallbackListener() {

		            @Override
		            public void onLogoutSuccess() {
//		                Util.alert(mContext, "logout ok");
		                Log.i("cocos2d-x debug info", "logout ok");
		            }

		            @Override
		            public void onLogoutError(DownjoyError error) {
		                int errorCode=error.getMErrorCode();
		                String errorMsg=error.getMErrorMessage();

//		                Util.alert(mContext, "onLogoutError:" + errorCode + "|" + errorMsg);
		                Log.i("cocos2d-x debug info", "onLogoutError:" + errorCode + "|" + errorMsg);
		            }

		            @Override
		            public void onError(Error error) {
//		                Util.alert(mContext, "onError:" + error.getMessage());
		                Log.i("cocos2d-x debug info", "onError:" + error.getMessage());
		            }
		        });
			}
		});
	}
	
	public static void DJEnterCenter()
	{
		((Activity)mContext).runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
                downjoy.openMemberCenterDialog(mContext, new CallbackListener() {

                    @Override
                    public void onMemberCenterBack() {
//                        Util.alert(mContext, "onMemberCenterBack");
                        Log.i("cocos2d-x debug info", "onMemberCenterBack");
                    }

                    @Override
                    public void onMemberCenterError(DownjoyError error) {
                        int errorCode=error.getMErrorCode();
                        String errorMsg=error.getMErrorMessage();

//                        Util.alert(mContext, "onMemberCenterError:" + errorCode + "|" + errorMsg);
                        Log.i("cocos2d-x debug info", "onMemberCenterError:" + errorCode + "|" + errorMsg);
                    }

                    @Override
                    public void onError(Error error) {
                        String errorMessage=error.getMessage();
//                        Util.alert(mContext, "onError:" + errorMessage);
                        Log.i("cocos2d-x debug info", "onError:" + errorMessage);
                    }
                });
			}
			
		});
	}
	
	public static void DJPurchase(String order, String productName, int price)
	{
		final String finalOrder = order;
		final String finalProductName = productName;
		final float finalPrice = price;
//		final float finalPrice = 0.01f;
		
		((Activity)mContext).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub

//                float money=0.1f; // 商品价格，单位：元
//                String productName="测试商品"; // 商品名称
//                String extInfo="123"; // CP自定义信息，多为CP订单号

                // 打开支付界面,获得订单号
                String orderNo=downjoy.openPaymentDialog(mContext, finalPrice, finalProductName, finalOrder, new CallbackListener() {

                    @Override
                    public void onPaymentSuccess(String orderNo) {
//                        Util.alert(mContext, "payment success! \n orderNo:" + orderNo);
                        Log.i("cocos2d-x debug info", "payment success! \n orderNo:" + orderNo);
                        
                        onDJPurchaseSuccess();
                    }

                    @Override
                    public void onPaymentError(DownjoyError error, String orderNo) {
                        int errorCode=error.getMErrorCode();
                        String errorMsg=error.getMErrorMessage();

//                        Util.alert(mContext, "onPaymentError:" + errorCode + "|" + errorMsg + "\n orderNo:" + orderNo);
                        Log.i("cocos2d-x debug info", "onPaymentError:" + errorCode + "|" + errorMsg + "\n orderNo:" + orderNo);
                        
		                if(errorCode == 103)
		                	onDJPurchaseCancel();
		                else
		                	onDJPurchaseFail(errorMsg);
                    }

                    @Override
                    public void onError(Error error) {
//                        Util.alert(mContext, "onError:" + error.getMessage());
                        Log.i("cocos2d-x debug info", "onError:" + error.getMessage());
                    }
                });
			}
		});
	}
	
	public static native void onDJLoginSuccess(String appid, String uid, String token, String nickname);
	
	public static native void onDJLoginCancel();
	
	public static native void onDJLoginFail(String error);
	
	public static native void onDJPurchaseSuccess();
	
	public static native void onDJPurchaseCancel();
	
	public static native void onDJPurchaseFail(String error);
}