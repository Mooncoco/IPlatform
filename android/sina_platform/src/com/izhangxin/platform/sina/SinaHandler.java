package com.izhangxin.platform.sina;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.StatusesAPI;
import com.weibo.sdk.android.net.RequestListener;

public class SinaHandler 
{
	private static boolean m_bSinaInited;
	private static Weibo mWeibo;
	private static String CONSUMER_KEY = "1479868089";	// 1421996614替换为开发者的appkey，例如"382298615";
	private static final String REDIRECT_URL = "http://www.sina.com";
	public static Oauth2AccessToken accessToken ;
	public static final String TAG = "Sina_SDK";
	private static Context context;
	private AuthDialogListener auth_dialog_listener = new AuthDialogListener();

	private static SinaHandler instance = null;
	
	public static SinaHandler getInstance()
	{
		if(instance != null)
		{
			return instance;
		}
		else
		{
			return new SinaHandler();
		}
		
	}
	public static void setContext(Context context,String key)
	{
		SinaHandler.context = context;
		m_bSinaInited = false;
		mWeibo = null;
		//CONSUMER_KEY = key;
	}
	
	public static void SinaPlatformInit()
	{
		accessToken = AccessTokenKeeper.readAccessToken(AccessTokenKeeper.PREFERENCES_SINA_NAME,context);
		
		if(!accessToken.isSessionValid())
		{
			if(!m_bSinaInited)
			{
				Log.i(TAG, "==============>SinaPlatformInit<<<--------------"+m_bSinaInited+"key:"+CONSUMER_KEY);
				m_bSinaInited = !m_bSinaInited;
				mWeibo = Weibo.getInstance(CONSUMER_KEY, REDIRECT_URL);
			}
		}
	}

	public static void SinaPlatformLogin()
	{
        if(accessToken.isSessionValid()){
        	
        	Log.i(TAG, "access_token:"+accessToken.getToken()+",uid:"+accessToken.getmUid());  
        	
        	SinaHandler.onSinaLoginSuccess(accessToken.getmUid(),accessToken.getToken());	
        }
        else
        {
        	Log.i(TAG, "==============>SinaPlatformLogin<===========");
			((Activity)context).runOnUiThread(new Runnable()
			{
				public void run()
				{
					mWeibo.authorize(context, SinaHandler.getInstance().auth_dialog_listener);
				}
			});
        }
	}
	
	public static void SinaPlatformLogout()
	{
		Log.d("SinaPlatformLogout", "SinaPlatformLogout");

	}
	
	class AuthDialogListener implements WeiboAuthListener {

		@Override
		public void onComplete(Bundle values) {
			Log.i(TAG, "com.weibo.sdk.android.api.WeiboAPI WeiboAuthListener onComplete");
			
			String token = values.getString("access_token");
			
			String expires_in = values.getString("expires_in");
			
			String uid = values.getString("uid");
			
			accessToken = new Oauth2AccessToken(token, expires_in, uid);
	
			if (accessToken.isSessionValid()) {
				
				AccessTokenKeeper.keepAccessToken(AccessTokenKeeper.PREFERENCES_SINA_NAME,context, accessToken);
				Log.i("result_success:",""+uid);
				SinaHandler.onSinaLoginSuccess(uid,token);
				
				//String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date(accessToken.getExpiresTime()));
			}
		}

		@Override
		public void onError(WeiboDialogError e) {
			Log.i(TAG, "Auth error : " + e.getMessage());
			SinaHandler.onSinaLoginFail();
		}

		@Override
		public void onCancel() {
			Log.i(TAG, "Auth cancel : ");
			SinaHandler.onSinaLoginCancel();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Log.i(TAG, "Auth Exception : " + e.getMessage());
			SinaHandler.onSinaLoginFail();
		}
	}
	
	public static native void onSinaLoginSuccess(String uid,String token);

	public static native void onSinaLoginFail();

	public static native void onSinaLoginCancel();
	
	/**
	 * 新浪微博分享
	 * @param pStr
	 * @param buf
	 * @param w
	 * @param h
	 * @return
	 * @throws IOException
	 */
	public static int SinaShareToThirdPlatformWithRGB(final String pStr, int[] buf, int w, int h) throws IOException
	{
		Bitmap bitmap = Bitmap.createBitmap(buf, w, h, Bitmap.Config.ARGB_8888);
		
		Matrix matrix = new Matrix(); 
		float scaleWidth = 480.0f / bitmap.getWidth();
		float scaleHeight =  320.0f / bitmap.getHeight();
		matrix.postScale(scaleWidth, scaleHeight);  
		
		final Bitmap bitmapScaled = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);  
		
		String fileName = "";
		try
		{
			fileName = saveMyBitmap(bitmapScaled);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	    
		if("".equals(fileName))
		{
			((Activity) context).runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					
					Toast.makeText(context, "分享失败,请确认有足够的存储空间!", Toast.LENGTH_SHORT).show();
				}
			});
			
			return -1;
		}
	    
		Log.d("SinaShareWithScreenShot", pStr);
		
		StatusesAPI api = new StatusesAPI(accessToken);
	
		api.upload(pStr,fileName, "0", "0", new RequestListener(){
		//api.update( pStr, "90.0", "90.0", new RequestListener(){
			@Override
			public void onComplete(String response) {
				
				Log.d("SinaShareWithScreenShot", "Success");
				
				((Activity) context).runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						
						Toast.makeText(context, "恭喜,已成功分享到新浪微博", Toast.LENGTH_SHORT).show();
					}
				});
			}

			@Override
			public void onError(WeiboException e) {
				
				((Activity) context).runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
				
						Toast.makeText(context, "分享失败,请稍后再试.", Toast.LENGTH_SHORT).show();
					}
				});

				Log.d("SinaShareWithScreenShot", "error"+e.getMessage());
			}

			@Override
			public void onIOException(IOException e) {
				
				Log.d("SinaShareToThirdPlatformWithScreenShot", "exception");
				
				((Activity) context).runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						
						Toast.makeText(context, "分享失败,请稍后再试.", Toast.LENGTH_SHORT).show();
					}
				});
			}
			
		});
		
		return 1;
	}
	
	/**
	 *  何存Bitmap到本地，sdk接口文档中貌似只有一个本地file路径... 
	 * @param bitmap
	 * @return
	 * @throws IOException
	 */
	public static String saveMyBitmap(Bitmap bitmap) throws IOException {
		
		String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
		String fileName = "/sdcard/com.bdo/weibo_" + date + ".png";
        File f = new File(fileName);
        f.createNewFile();
        FileOutputStream fOut = null;
        try {
                fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
                fOut.flush();
        } catch (IOException e) {
                e.printStackTrace();
        }
        try {
                fOut.close();
        } catch (IOException e) {
                e.printStackTrace();
        }
        return fileName;
	}
	
	/**
	 * No use 
	 * @param pStr
	 * @param buf
	 * @return
	 */
	public static int SinaShareToThirdPlatformWithFile(final String content, final String path)
	{	
		if("".equals(content))
		{
			((Activity)context).runOnUiThread(new Runnable(){
				public void run()
				{	
					Toast.makeText(context, "分享失败请联系客服!", Toast.LENGTH_SHORT).show();
				}
			});
			
			return 1;
		}
		
		Bitmap image = getImageFromAssetsFile(path);
		
		String localPath = null;
		
		if(image != null)
		{
			try {
				localPath = saveMyBitmap(image);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				Toast.makeText(context, "读取"+path+"资源失败！", Toast.LENGTH_SHORT).show();
				return -1;
			}
		}
		
		if(localPath == null)
		{
			Toast.makeText(context, "读取"+path+"资源失败！", Toast.LENGTH_SHORT).show();
			return -1;
		}
		
		final String finalPath = localPath;
		
		((Activity)context).runOnUiThread(new Runnable()
		{
			public void run()
			{	
				StatusesAPI api = new StatusesAPI(accessToken);
				api.upload(content, finalPath, "0", "0", new RequestListener(){

					@Override
					public void onComplete(String response) {
						// TODO Auto-generated method stub
						((Activity)context).runOnUiThread(new Runnable() 
						{
							public void run()
							{
								Toast.makeText(context, "恭喜,已成功分享到新浪微博！", Toast.LENGTH_SHORT).show();
							}
						});
					}

					@Override
					public void onError(final WeiboException e) {
						
						((Activity)context).runOnUiThread(new Runnable() 
						{
							public void run()
							{
								// TODO Auto-generated method stub
								System.out.println("===========================>e.getMessage():" + e.getMessage());
								Toast.makeText(context, "分享失败,请稍后再试!", Toast.LENGTH_SHORT).show();
							}
						});
					}

					@Override
					public void onIOException(IOException e) {
						// TODO Auto-generated method stub
						
					}
					
				});
			}
		});
		return 1;
	}
	
	/**
	     * 从Assets中读取图片
	     */
	public static  Bitmap getImageFromAssetsFile(String fileName)
	{
	    Bitmap image = null;
	    AssetManager am = context.getResources().getAssets();
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
}