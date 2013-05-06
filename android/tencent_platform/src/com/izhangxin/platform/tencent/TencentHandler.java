package com.izhangxin.platform.tencent;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tencent.tauth.TAuthView;
import com.tencent.tauth.TencentOpenAPI;
import com.tencent.tauth.bean.OpenId;
import com.tencent.tauth.http.Callback;

public class TencentHandler
{
	private static final String TAG ="TAuth_";
	public static final int REQUEST_PICK_PICTURE = 1001;
	
	private static final String CALLBACK = "auth://tauth.qq.com/";
	
	public static String mAppid = "100289601";//申请时分配的appid
	private static String scope = "get_user_info,add_share";//授权范围
	private static AuthReceiver receiver;
	
	//public static String mAccessToken, mOpenId;
	public static Oauth2AccessToken accessTencentToken ;
	
	private static ProgressDialog progressDialog;
	
	private static boolean m_bTencentInited;
	
	public static Context context;

	public static void setContext(Context context)
	{
		TencentHandler.context = context;
		m_bTencentInited = false;
		//TencentPlatformInit();
	}
	
	public static void TencentPlatformInit()
	{
		accessTencentToken = AccessTokenKeeper.readAccessToken(AccessTokenKeeper.PREFERENCES_TENCENT_NAME, context); 
		
		if(!accessTencentToken.isSessionValid())
		{
			if(!m_bTencentInited)
			{	
				Log.i(TAG,"tencent:::::::::::::::::::init====>"+m_bTencentInited);
				registerIntentReceivers();	
			
				m_bTencentInited = !m_bTencentInited;
			}
		}
	}

	public static void TencentPlatformLogin()
	{
		if(accessTencentToken.isSessionValid())
		{
			TencentHandler.onTencentLoginSuccess(accessTencentToken.getmUid(), accessTencentToken.getToken());
		}
		else
		{
			Intent intent = new Intent(context, com.tencent.tauth.TAuthView.class);
			
			intent.putExtra(TAuthView.CLIENT_ID, mAppid);
			intent.putExtra(TAuthView.SCOPE, scope);
			intent.putExtra(TAuthView.TARGET, "_self");
			intent.putExtra(TAuthView.CALLBACK, CALLBACK);
			
			((Activity)context).startActivity(intent);
		}
	}

	public static void TencentPlatformLogout()
	{
		Log.d("SinaPlatformLogout", "SinaPlatformLogout");
		
		AccessTokenKeeper.clear(AccessTokenKeeper.PREFERENCES_TENCENT_NAME, context);
	}
	
	/*
	 * tencent分享，接口中所分享的网页资源的代表性图片链接，所以先把截屏上传给bdo，
	 * 把bdo result的resultUrl传给tencent------- 
	 */
	public static int TencentShareToThirdPlatformWithRGB(final String pStr,  int[] buf,  int w,  int h) throws IOException, JSONException
	{
		Bundle bundle = null;
		bundle = new Bundle();
		bundle.putString("title", "掌心游-首页 ");//必须。feeds的标题，最长36个中文字，超出部分会被截断。
		bundle.putString("url", "http://e.weibo.com/zhangxinyou" + "#" + System.currentTimeMillis());//必须。分享所在网页资源的链接，点击后跳转至第三方网页， 请以http://开头。
		bundle.putString("comment", pStr);//用户评论内容，也叫发表分享时的分享理由。禁止使用系统生产的语句进行代替。最长40个中文字，超出部分会被截断。
		bundle.putString("summary", "中国最大最全的手机网络游戏平台");//所分享的网页资源的摘要内容，或者是网页的概要描述。 最长80个中文字，超出部分会被截断。
		//bundle.putString("images", url);//所分享的网页资源的代表性图片链接"，请以http://开头，长度限制255字符。多张图片以竖线（|）分隔，目前只有第一张图片有效，图片规格100*100为佳。
		bundle.putString("type", "4");//分享内容的类型。
		//bundle.putString("playurl", "http://player.youku.com/player.php/Type/Folder/Fid/15442464/Ob/1/Pt/0/sid/XMzA0NDM2NTUy/v.swf");//长度限制为256字节。仅在type=5的时候有效。
		TencentOpenAPI.addShare(accessTencentToken.getToken(), mAppid, accessTencentToken.getmUid(), bundle, new Callback() {
			
			@Override
			public void onSuccess(final Object obj) {
				((Activity) context).runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						
						Toast.makeText(context, "恭喜,已成功分享到QQ空间", Toast.LENGTH_SHORT).show();
					}
				});
			}
			
			@Override
			public void onFail(final int ret, final String msg) {
				((Activity) context).runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						
						Toast.makeText(context, "分享失败,请稍后再试!", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
		/*
		Bitmap bitmap = Bitmap.createBitmap(buf, w, h, Bitmap.Config.ARGB_8888);
		
		Matrix matrix = new Matrix(); 
		float scaleWidth = 480.0f / bitmap.getWidth();
		float scaleHeight =  320.0f / bitmap.getHeight();
		matrix.postScale(scaleWidth, scaleHeight);  
		
		final Bitmap bitmapScaled = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);  
		
		Log.d("TencentShareScreenShot", pStr);
		
		/*把截屏保存到本地
		String fileName = "";
		try
		{
			fileName = saveMyBitmap(bitmapScaled);
		}
		catch(Exception e)
		{
			fileName = "";
			e.printStackTrace();
		}
		
		if("".equals(fileName))
		{
			((Activity) context).runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					
					Toast.makeText(context, "分享失败,请确认是否有足够的空间.", Toast.LENGTH_SHORT).show();
				}
			});
			
			return -1;
		}
		File file = new File(fileName);
		
		/*把Bitmap截屏上传给bdo，----应该是可以不用先保存在本地的吧？
		String newFileName = ""; 
		try
		{
			newFileName = uploadFileUtil.uploadFile(file, "http://fileupload.hiigame.com:8080/FileUploadControl");
		}
		catch(Exception e)
		{
			newFileName = "";
			e.printStackTrace();
		}
		
		if("".equals(newFileName))
		{
			((Activity) context).runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					
					Toast.makeText(context, "分享失败,请与运营商联系.", Toast.LENGTH_SHORT).show();
				}
			});
			
			return -1;
		}
		
		String url = "";
		
		try{
			
			JSONObject json = new JSONObject(newFileName);
			url = json.getString("middleImgUrl");
		}
		catch(Exception e)
		{
			url = "";
			e.printStackTrace();
		}
			
		if("".equals(url))
		{
			((Activity) context).runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					
					Toast.makeText(context, "分享失败,请稍后再试.", Toast.LENGTH_SHORT).show();
				}
			});
			
			return -1;
		}
		
		Log.d("TencentShareUrl:", url);
		
		if("".equals(newFileName) || newFileName.equals("null"))
		{
			((Activity) context).runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					
					Toast.makeText(context, "分享失败,请稍后再试.", Toast.LENGTH_SHORT).show();
				}
			});
		}
		else
		{
			Bundle bundle = null;
			bundle = new Bundle();
			bundle.putString("title", "掌心游-首页 ");//必须。feeds的标题，最长36个中文字，超出部分会被截断。
			bundle.putString("url", "http://www.izhangxin.com" + "#" + System.currentTimeMillis());//必须。分享所在网页资源的链接，点击后跳转至第三方网页， 请以http://开头。
			bundle.putString("comment", pStr);//用户评论内容，也叫发表分享时的分享理由。禁止使用系统生产的语句进行代替。最长40个中文字，超出部分会被截断。
			bundle.putString("summary", "中国最大最全的手机网络游戏平台");//所分享的网页资源的摘要内容，或者是网页的概要描述。 最长80个中文字，超出部分会被截断。
			bundle.putString("images", url);//所分享的网页资源的代表性图片链接"，请以http://开头，长度限制255字符。多张图片以竖线（|）分隔，目前只有第一张图片有效，图片规格100*100为佳。
			bundle.putString("type", "4");//分享内容的类型。
			//bundle.putString("playurl", "http://player.youku.com/player.php/Type/Folder/Fid/15442464/Ob/1/Pt/0/sid/XMzA0NDM2NTUy/v.swf");//长度限制为256字节。仅在type=5的时候有效。
			TencentOpenAPI.addShare(accessTencentToken.getToken(), mAppid, accessTencentToken.getmUid(), bundle, new Callback() {
				
				@Override
				public void onSuccess(final Object obj) {
					((Activity) context).runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							
							Toast.makeText(context, "恭喜,已成功分享到QQ空间", Toast.LENGTH_SHORT).show();
						}
					});
				}
				
				@Override
				public void onFail(final int ret, final String msg) {
					((Activity) context).runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							
							Toast.makeText(context, "分享失败,请稍后再试.", Toast.LENGTH_SHORT).show();
						}
					});
				}
			});
		}
		*/
		return 1;
	}
	
	public static String saveMyBitmap(Bitmap bitmap) throws IOException {
		
		String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
		String fileName = "/sdcard/com.bdo/tencent_" + date + ".png";
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
	
	private static void registerIntentReceivers() {
		receiver =  new AuthReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(TAuthView.AUTH_BROADCAST);
		context.registerReceiver(receiver, filter);
	}
	
	private static void unregisterIntentReceivers() {
		context.unregisterReceiver(receiver);
	}
	
	public static void setOpenIdText(String txt) {
		
		accessTencentToken.setmUid(txt);
	}
	
	public static native void onTencentLoginSuccess(String uid,String token);

	public static native void onTencentLoginFail();

	public static native void onTencentLoginCancel();
	
	/**
	 * tencent验证回调，通过广播方式---------------
	 * @author liuyuhong
	 *
	 */
	public static class AuthReceiver extends BroadcastReceiver {
    	
    	private static final String TAG="AuthReceiver";

    	@Override
    	public void onReceive(final Context context, Intent intent) {
 			Bundle exts = intent.getExtras();
        	String raw =  exts.getString("raw");
        	String access_token =  exts.getString(TAuthView.ACCESS_TOKEN);
        	String expires_in =  exts.getString(TAuthView.EXPIRES_IN);
        	String error_ret =  exts.getString(TAuthView.ERROR_RET);
        	String error_des =  exts.getString(TAuthView.ERROR_DES);
        	Log.i(TAG, String.format("raw: %s, access_token:%s, expires_in:%s", raw, access_token, expires_in));
        	
        	if (access_token != null) {
        		
        		accessTencentToken.setToken(access_token);
        		
        		accessTencentToken.setExpiresIn(expires_in);
        		
        		//((TextView)findViewById(R.id.access_token)).setText(access_token);
//        		TDebug.msg("正在获取OpenID...", getApplicationContext());
   
        		if(progressDialog == null)
				{
					progressDialog = ProgressDialog.show(context, "请求中,请稍等...", "请求中,请稍等...", true, false); 
				}
        		//用access token 来获取open id
				TencentOpenAPI.openid(access_token, new Callback() {
					@Override
					public void onSuccess(final Object obj) {
						((Activity) context).runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if(progressDialog != null)
								{
									progressDialog.dismiss();
									progressDialog = null;
								}
								
								setOpenIdText(((OpenId)obj).getOpenId());
								
								Log.i(TAG,"result_success:openid,accessToken:"+accessTencentToken.getmUid()+","+accessTencentToken.getToken());
								
								AccessTokenKeeper.keepAccessToken(AccessTokenKeeper.PREFERENCES_TENCENT_NAME,context, accessTencentToken);
								
								TencentHandler.onTencentLoginSuccess(accessTencentToken.getmUid(), accessTencentToken.getToken());
							}
						});
					}
					@Override
					public void onFail(int ret, final String msg) {
						((Activity) context).runOnUiThread(new Runnable() {
							@Override
							public void run() {
								if(progressDialog != null)
								{
									progressDialog.dismiss();
									progressDialog = null;
								}
								
								Log.i(TAG,"result_fail"+msg);
								
								TencentHandler.onTencentLoginFail();
							}
						});
					}
				});
			}
		
        	if (error_ret != null) {
        		
        		Log.i(TAG,"result_error"+error_ret+","+error_des);
        		
        		TencentHandler.onTencentLoginFail();
			}
    	}
    }
	
	public static int TencentShareToThirdPlatformWithImageBuffer(final String pStr, byte[] buf)
	{
		final Bitmap bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(buf), null, null);
		Log.d("TencentShareToThirdPlatformWithImageBuffer","=======================TencentShareToThirdPlatformWithFile");
		String fileName = null;
		try {
			fileName = saveMyBitmap(bitmap);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(fileName == null || "".equals(fileName))
		{
			((Activity) context).runOnUiThread(new Runnable() {		
				@Override
				public void run() {
					Toast.makeText(context, "分享失败,请确认有足够的存储空间！", Toast.LENGTH_SHORT).show();
				}
			});
	
			return 1;
		}
		
		File file = new File(fileName);
		/*把Bitmap截屏上传给bdo，----应该是可以不用先保存在本地的吧？*/
		String newFileName = UpUtil.uploadFile(file, "http://upload.bdo.hiigame.com:8080/FileUploadControl");
		
		if(newFileName == null || "".equals(newFileName))
		{
			((Activity) context).runOnUiThread(new Runnable() {		
				@Override
				public void run() {
					Toast.makeText(context, "分享失败,请联系客服！", Toast.LENGTH_SHORT).show();
				}
			});
	
			return 1;
		}
		
		Log.d("TencentShareNewFileName:", newFileName);
		
		JSONObject json = null;
		try {
			json = new JSONObject(newFileName);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String url = null;
		try {
			url = json.getString("middleImgUrl");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(url == null || "".equals(url))
		{
			((Activity) context).runOnUiThread(new Runnable() {		
				@Override
				public void run() {
					Toast.makeText(context, "分享失败,请稍后再试！", Toast.LENGTH_SHORT).show();
				}
			});
	
			return 1;
		}
		
		Bundle bundle = null;
		bundle = new Bundle();
		bundle.putString("title", "掌心游-首页 ");//必须。feeds的标题，最长36个中文字，超出部分会被截断。
		bundle.putString("url", "http://www.izhangxin.com" + "#" + System.currentTimeMillis());//必须。分享所在网页资源的链接，点击后跳转至第三方网页， 请以http://开头。
		bundle.putString("comment", pStr);//用户评论内容，也叫发表分享时的分享理由。禁止使用系统生产的语句进行代替。最长40个中文字，超出部分会被截断。
		bundle.putString("summary", "中国最大最全的手机网络游戏平台");//所分享的网页资源的摘要内容，或者是网页的概要描述。 最长80个中文字，超出部分会被截断。
		bundle.putString("images", url);//所分享的网页资源的代表性图片链接"，请以http://开头，长度限制255字符。多张图片以竖线（|）分隔，目前只有第一张图片有效，图片规格100*100为佳。
		bundle.putString("type", "4");//分享内容的类型。
				TencentOpenAPI.addShare(accessTencentToken.getToken(), mAppid, accessTencentToken.getmUid(), bundle, new Callback() {
					
					@Override
					public void onSuccess(final Object obj) {
						((Activity) context).runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								
								Toast.makeText(context,"分享到成功！",Toast.LENGTH_SHORT).show();
							}
						});
					}
					
					@Override
					public void onFail(final int ret, final String msg) {
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
	
	public static int TencentShareToThirdPlatformWithURL(final String pStr, final String pURL)
	{
		Bundle bundle = null;
		bundle = new Bundle();
		bundle.putString("title", "掌心游-首页 ");//必须。feeds的标题，最长36个中文字，超出部分会被截断。
		bundle.putString("url", "http://www.izhangxin.com" + "#" + System.currentTimeMillis());//必须。分享所在网页资源的链接，点击后跳转至第三方网页， 请以http://开头。
		bundle.putString("comment", pStr);//用户评论内容，也叫发表分享时的分享理由。禁止使用系统生产的语句进行代替。最长40个中文字，超出部分会被截断。
		bundle.putString("summary", "中国最大最全的手机网络游戏平台");//所分享的网页资源的摘要内容，或者是网页的概要描述。 最长80个中文字，超出部分会被截断。
		bundle.putString("images", pURL);//所分享的网页资源的代表性图片链接"，请以http://开头，长度限制255字符。多张图片以竖线（|）分隔，目前只有第一张图片有效，图片规格100*100为佳。
		bundle.putString("type", "4");//分享内容的类型。
		//bundle.putString("playurl", "http://player.youku.com/player.php/Type/Folder/Fid/15442464/Ob/1/Pt/0/sid/XMzA0NDM2NTUy/v.swf");//长度限制为256字节。仅在type=5的时候有效。
		TencentOpenAPI.addShare(accessTencentToken.getToken(), mAppid, accessTencentToken.getmUid(), bundle, new Callback() {
			
			@Override
			public void onSuccess(final Object obj) {
				((Activity) context).runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						
						Toast.makeText(context, "恭喜,已成功分享到QQ空间", Toast.LENGTH_SHORT).show();
						
					}
				});
			}
			
			@Override
			public void onFail(final int ret, final String msg) {
				((Activity) context).runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						
						Toast.makeText(context, "分享失败,请稍后再试. ID "+ret, Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
		
		return 1;
	}

	public static void onDestroy() {
		// TODO Auto-generated method stub
		if(receiver != null)
			unregisterIntentReceivers();
	}
}
