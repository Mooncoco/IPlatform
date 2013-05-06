package com.izhangxin.wyx.android.handler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.security.spec.EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.izhangxin.wyx.android.handler.HttpUtils;
import com.izhangxin.wyx.android.handler.UpUtil;
import com.weibo.net.DialogError;
import com.weibo.net.WeiboDialogListener;
import com.weibo.net.WeiboException;

import com.weiyouxi.android.sdk.Wyx;
import com.weiyouxi.android.sdk.WyxConfig;
import com.weiyouxi.android.sdk.ui.WyxActivity;
import com.weiyouxi.android.sdk.ui.menu.MenuBar;
import com.weiyouxi.android.sdk.util.WyxAsyncRunner;
import com.weiyouxi.android.sdk.util.WyxUtil;
import com.weiyouxi.android.sdk.util.WyxAsyncRunner.ResponseListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

public class IPlatformAndroidHandler {
	
	private static IPlatformAndroidHandler instance = null;
	
	private Context mContext = null;
	
	private Wyx mWyx = null;
	
	private final int PAY = 1;
	
	private final int DIALOGID = 100;
	
	private String profile_img_url = null;
	
	private static final String TEST_FROM = "7884";
	
	private boolean isLogined = false;
	
	public static IPlatformAndroidHandler getInstance() {
		if(instance == null)
			instance = new IPlatformAndroidHandler();
		
		return instance;
	}
	
	public Wyx getWyxInstance() {
		return mWyx;
	}
	
	public void onPause() {
		if(mWyx != null)
			mWyx.onPause();
	}
	
	public void onResume() {
		if(mWyx != null)
			mWyx.onResume();
	}

	public void setContext(Context context) {
		mContext = context;
	}

	private void init() {
		
		if(mWyx == null) {
		
			mWyx = Wyx.getInstance();

			// 第二个参数控制menubar需要显示在上面还是下面
			mWyx.registerWyxResource(mContext);
		}
	}
	
	private void clear()
	{
		isLogined = false;
	}
	
	private void initWithParams(String app_id, String app_key, String pay_id){
		
		if(mWyx == null) {
			
			init();
			
			// 重新设置参数
			mWyx.init(mContext, app_id, app_key, pay_id);
			// 在此需要设置是webview还是系统内置浏览器来打开auth2.0页面
			// 此函数必须在mWyx.init(this, appKey, appSecret, payId)之后调用
			mWyx.useBrowserWhenAuth(false);
		}
	}
	
	private void login() {
		
		if(mContext != null) {

			((Activity)mContext).runOnUiThread(new Runnable(){
				
				public void run() {
					mWyx.authorize((Activity) mContext, new AuthDialogListener());
				}
			});
		}
	}
	
	public void logout() {
		
		if(mWyx != null)
			mWyx.clearSessionKey();
		
		profile_img_url = null;
	}
	
	public boolean isLogined() {
		
		if(mWyx != null && mWyx.isLogin() && isLogined)
		{
			Log.i("islogined", "YES");
			return true;
		}
		else
		{
			Log.i("islogined", "NO");
			return false;
		}
	}
	
	public void pay(String order, String name, int saleMoney) {
		
		final String desc = name;
		final int amount = saleMoney * 100;
		
		mWyx.getToken(amount, order, name, new ResponseListener() {
			@Override
			public void onComplete(String response) {
				final String getTokenResponse = response;
				JSONObject jsonObject = WyxUtil.parseToJSON(response);
				boolean sessionInvalid = sessionInvalid(jsonObject);
				if (sessionInvalid) { return; }

				if(jsonObject != null && jsonObject.has("error_code")) {

					try {
						String error = jsonObject.getString("error");
						Log.i("error", error);
						onPayResultFail(PAY_FAIL, error);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return;
				}
				
				Log.i("getToken() finish", response);
				
				String order_id = Wyx.getInstance().getOrderId();
				String order_uid = "";
				String token = "";
				try {
					order_uid = jsonObject.getString("order_uid");
					token = jsonObject.getString("token");

					Wyx.getInstance().registerOrder(order_id, order_uid, token, amount, desc, TEST_FROM, new ResponseListener() {

						@Override
						public void onComplete(String response) {
							JSONObject jsonObject = WyxUtil.parseToJSON(response);

							// code==0 表示为注册成功
							int code = 1;
							try {
								code = jsonObject.getInt("code");
							}
							catch (JSONException e) {
								e.printStackTrace();
							}

							if (code == 0) {
								Bundle params = new Bundle();
								params.putString("response", getTokenResponse);
								params.putString("desc", desc);
								params.putString("amount", String.valueOf(amount));
								
								Message msg = new Message();
								msg.what = PAY;
								msg.setData(params);
								msg.obj = "payment";
								mHandler.sendMessage(msg);
							}
						}

						@Override
						public void onFail(Exception e) {
							// TODO Auto-generated method stub

						}
					});

				}
				catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFail(Exception e) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	private void initHeadFace()
	{	
		if(profile_img_url == null)
		{
			mWyx.showBatch(mWyx.getUserId(), new ResponseListener() {
				
				@Override
				public void onFail(Exception e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onComplete(String response) {
					// TODO Auto-generated method stub		
					Log.i("getHeadFace onComplete", response);
					
					JSONObject jsonObject = WyxUtil.parseToJSON(response);
					boolean sessionInvalid = sessionInvalid(jsonObject);
					if (sessionInvalid) {
						return;
					}
					
					if(jsonObject != null && jsonObject.has("error_code")) {
						try {
							Log.i("error", jsonObject.getString("error"));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return;
					}
					
					JSONObject jsonObject1 = null;
					try {
						jsonObject1 = jsonObject.getJSONObject(mWyx.getUserId());
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					if(jsonObject1 != null)
					{
						try {
							profile_img_url = jsonObject1.getString("profile_image_url");
							Log.i("initHeadFace", profile_img_url);
							downloadHeadFace(profile_img_url);
							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});
		}
		else
		{
			Log.i("initHeadFace", profile_img_url);
			downloadHeadFace(profile_img_url);
		}
	}
	
	public void shareWithImageUrl(String text, String path)
	{
		final String strText = text;
		
		if(mContext != null) {
			
			((Activity)mContext).runOnUiThread(new Runnable(){
				
				public void run() {
					Toast.makeText(mContext, "请稍后，正在为您分享..", 3).show();
				}
			});
		}

		Bitmap image = getImageFromAssetsFile(path);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.PNG, 100, baos);
		
		if(mWyx != null)
			mWyx.sendFeed(text, baos.toByteArray(), 0, new ResponseListener() {

				@Override
				public void onFail(Exception e) {
					// TODO Auto-generated method stub
					
					Log.i("share onFail", e.toString());
					
					final Exception e1 = e;
					
					if(mContext != null) {
						
						((Activity)mContext).runOnUiThread(new Runnable(){
							
							public void run() {
								Toast.makeText(mContext, "分享失败: "+e1.toString(), 3).show();
							}
						});
					}
				}
				
				@Override
				public void onComplete(String str) {
					// TODO Auto-generated method stub
					
					Log.i("share onComplete", str);
					
					if(mContext != null) {
						
						((Activity)mContext).runOnUiThread(new Runnable(){
							
							public void run() {
								Toast.makeText(mContext, "恭喜，成功分享到新浪微薄！", 3).show();
							}
						});
					}
				}
			});
	}
	
	/**
     * 从Assets中读取图片
     */
	public Bitmap getImageFromAssetsFile(String fileName)
	{
	    Bitmap image = null;
	    AssetManager am = mContext.getResources().getAssets();
	    try
	    {
	        InputStream is = am.open(fileName);
	        image = BitmapFactory.decodeStream(is);
	        is.close();
	    }
	    catch (IOException e)
	    {
	        e.printStackTrace();
	    }
	
	    return image;
	
	}
	
	public native void downloadHeadFace(String url);
	
	public void shareWithByteArray(final String pStr,  int[] buf,  int w,  int h)
	{
		Bitmap bitmap = Bitmap.createBitmap(buf, w, h, Bitmap.Config.ARGB_8888);
		
		Matrix matrix = new Matrix(); 
		float scaleWidth = 480.0f / bitmap.getWidth();
		float scaleHeight =  320.0f / bitmap.getHeight();
		matrix.postScale(scaleWidth, scaleHeight);  
		
		Bitmap bitmapScaled = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);  

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmapScaled.compress(Bitmap.CompressFormat.PNG, 100, baos);

		if(mContext != null) {
			
			((Activity)mContext).runOnUiThread(new Runnable(){
				
				public void run() {
					Toast.makeText(mContext, "请稍后，正在为您分享..", 3).show();
				}
			});
		}

		if(mWyx != null)
			mWyx.sendFeed(pStr, baos.toByteArray(), 0, new ResponseListener() {

				@Override
				public void onFail(Exception e) {
					// TODO Auto-generated method stub
					
					Log.i("share onFail", e.toString());
					
					final Exception e1 = e;
					
					if(mContext != null) {
						
						((Activity)mContext).runOnUiThread(new Runnable(){
							
							public void run() {
								Toast.makeText(mContext, "分享失败: "+e1.toString(), 3).show();
							}
						});
					}
				}
				
				@Override
				public void onComplete(String str) {
					// TODO Auto-generated method stub
					
					Log.i("share onComplete", str);
					
					if(mContext != null) {
						
						((Activity)mContext).runOnUiThread(new Runnable(){
							
							public void run() {
								Toast.makeText(mContext, "恭喜，成功分享到新浪微薄！", 3).show();
							}
						});
					}
				}
			});
	}
	
	private final int PAY_SUCCESS = 0;
	private final int PAY_CANCEL = 1;
	private final int PAY_FAIL = 2;
	
	public static native void onPayResult(int result);
	
	public static native void onPayResultFail(int result, String error);

	private final Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			if (null == msg.obj) {
				((Activity)mContext).showDialog(DIALOGID);
			}

			switch (msg.what) {
			case PAY:
				String response = msg.getData().getString("response");
				
				JSONObject jsonObject = WyxUtil.parseToJSON(response);

				String strParam = "order_id="+mWyx.getOrderId()+"&";

				try {
					strParam += "order_uid=" + jsonObject.getString("order_uid") + "&";
					strParam += "token=" + jsonObject.getString("token") + "&";
				}
				catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				strParam += "desc=" + msg.getData().getString("desc") + "&";
				strParam += "source=" + Wyx.getInstance().getAppKey() + "&";
				strParam += "amount=" + msg.getData().getString("amount") + "&";
				strParam += "ru=http://www.weibo.cn";
				
				byte[] postData = EncodingUtils.getBytes(strParam, "BASE64");
				
				String url = "http://game.weibo.com/m/payment/cnpayment";

				Wyx.getInstance().openPaymentWebView(url, postData, new WeiboDialogListener() {

					@Override
					public void onComplete(Bundle values) {
						// TODO Auto-generated method stub
						Log.i("payment", values.toString());
						
						if (values.getInt("paymentStatus") == WyxConfig.PAYMENT_DONE) {
							onPayResult(PAY_SUCCESS);
						}
					}

					@Override
					public void onCancel() {
						// TODO Auto-generated method stub
						Log.i("payment", "onCancel");
						onPayResult(PAY_CANCEL);
					}

					@Override
					public void onWeiboException(WeiboException e) {
						// TODO Auto-generated method stub
						Log.i("payment", "onWeiboException: "+e.toString());
						onPayResult(PAY_FAIL);
					}

					@Override
					public void onError(DialogError e) {
						// TODO Auto-generated method stub
						Log.i("payment", "onError: "+e.toString());
						onPayResult(PAY_FAIL);
					}
				});
				break;
			}
		}
	};
	

	/**
	 * 判断本地Session是否已过期，如过期则重新登录
	 * @param jsonObj
	 * @return
	 */
	private boolean sessionInvalid(JSONObject jsonObj) {
		try {
			if (jsonObj != null) {
				int errorCode = jsonObj.has("error_code") ? jsonObj.getInt("error_code") : 0;

				if (errorCode == 38 || errorCode == 39) {
					String error = jsonObj.getString("error");
					Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();

					Wyx.getInstance().clearSessionKey();

					if(mContext != null)
					{
						((Activity)mContext).runOnUiThread(new Runnable(){
							
							public void run()
							{
								mWyx.authorize((Activity) mContext, new AuthDialogListener());
							}
						});
					}
					return true;
				}
			}
		}
		catch (JSONException e1) {
			e1.printStackTrace();
		}
		return false;
	}
	
	public native void onLogin(String uid, String token);
	
	class AuthDialogListener implements WeiboDialogListener {

		@Override
		public void onComplete(Bundle values) {
			Log.i("AuthDialogListener", "onComplete !");
			Log.i("Bundle.toString()", values.toString());
			
			String userid = values.getString("userId");
			String access_token = values.getString("sessionKey");
			
			if(userid == null)
				userid = mWyx.getUserId();
			
			isLogined = true;
			
			onLogin(userid, access_token);
		}

		@Override
		public void onError(DialogError e) {
			Log.i("AuthDialogListener", "onError !");
			Log.i("DialogError.toString()", e.toString());
		}

		@Override
		public void onCancel() {
			Log.i("AuthDialogListener", "onCancel !");
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Log.i("AuthDialogListener", "onWeiboException !");
			Log.i("WeiboException.toString()", e.toString());
		}
	}
	
	public void upLoadHeadFace(String uploadUrl, String filePath, String pid) {
		
		File file = new File(filePath);
		
		Log.i("uploadUrl", "filePath: "+filePath);
		
		if(file.isDirectory() || !file.exists())
			return;
		
		String newFileName = UpUtil.uploadFile(file, "http://fileupload.hiigame.com:8080/FileUploadControl");
		if(newFileName != null && !"".equals(newFileName))
		{
			JSONObject json;
			try {
				json = new JSONObject(newFileName);
				String url_path = json.getString("imgUrl");
				
				String str_url = uploadUrl;

				Map<String,String> params = new HashMap<String,String>();
				params.put("pid", pid);
				params.put("face", url_path);
				
				try {
					ArrayList list = (ArrayList)HttpUtils.URLPost(str_url, params);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//
	//--------------jni method---------------
	
	public static void initPlatform() {
		
		Log.i("IPlatformAndroid", "initPlatform !");
		
		getInstance().init();
	}
	
	public static void initPlatformWithParams(String app_id, String app_key, String pay_id) {
		
		Log.i("IPlatformAndroid", "initPlatformWithParams : app_id "+app_id+" app_key "+app_key+" pay_id "+pay_id);
		
		getInstance().initWithParams(app_id, app_key, pay_id);
	}
	
	public static void loginPlatform() {
		
		Log.i("IPlatformAndroid", "loginPlatform !");

		getInstance().login();
	}
	
	public static void logoutPlatform() {
		
		Log.i("IPlatformAndroid", "logoutPlatform !");
		
		getInstance().logout();
	}
	
	public static boolean IsLoginedPlatform() {
		
		Log.i("IPlatformAndroid", "IsLoginedPlatform !");
		
		return getInstance().isLogined();
	}
	
	public static void clearPlatform()
	{
		Log.i("IPlatformAndroid", "clearPlatform !");
		
		getInstance().clear();
	}
	
	public static void payPlatform(String order, String name, int saleMoney)
	{
		Log.i("IPlatformAndroid", "payPlatform !");
		
		getInstance().pay(order, name, saleMoney);
	}
	
	public static void initHeadFacePlatform()
	{
		Log.i("IPlatformAndroid", "initHeadFacePlatform !");
		
		getInstance().initHeadFace();
	}
	
	public static void shareWithByteArrayPlatform(String text, int[] buff, int w, int h)
	{
		Log.i("IPlatformAndroid", "getHeadFacePlatform !");
		
		getInstance().shareWithByteArray(text, buff, w, h);
	}
	
	public static void uploadHeadFacePlatform(String uploadUrl, String filePath, String guid)
	{
		Log.i("IPlatformAndroid", "uploadHeadFacePlatform !");
		
		getInstance().upLoadHeadFace(uploadUrl, filePath, guid);
	}
	
	public static void shareWithImageUrlPlatform(String text, String path)
	{
		Log.i("IPlatformAndroid", "shareWithImageUrlPlatform");
		
		getInstance().shareWithImageUrl(text, path);
	}
}
