package com.izhangxin.platform.lenovo;

import com.example.lenovo_platform.R;
import com.iapppay.mpay.ifmgr.IPayResultCallback;
import com.iapppay.mpay.ifmgr.SDKApi;
import com.iapppay.mpay.tools.PayRequest;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class LenovoPlatformInterface
{
	private static String TAG = "lenovo_platform";

	private static Context mContext = null;

	public static void setContext(Context con) { 
		mContext = con;
	}

	private static String notifyurl = "http://t.mall.hiigame.com/iapp/pay/notify";
	private static String appid = null;
	//商品密钥
	private static String appkey = null;
	
	public static boolean IAPInit()
	{
		appid = mContext.getString(R.string.lenovo_app_id);
		appkey = mContext.getString(R.string.lenovo_app_key);
		
		SDKApi.init((Activity)mContext, SDKApi.LANDSCAPE, appid);
		
		return true;
	}

	public static void LENOVOPurchase(String order, String productName, int price, String productID)
	{		
		Log.i(TAG, "LenovoPurchase");
		
		PayRequest payRequest = new PayRequest();
		payRequest.addParam("notifyurl", notifyurl);
		payRequest.addParam("appid", appid);
		payRequest.addParam("waresid", productID);
		payRequest.addParam("quantity", 1);
		payRequest.addParam("exorderno", order);
		payRequest.addParam("price", price);
		payRequest.addParam("cpprivateinfo", "");
		
		final String paramUrl = payRequest.genSignedUrlParamString(appkey);
		
		((Activity)mContext).runOnUiThread(new Runnable() {
			
			@Override
			public void run()
			{
				SDKApi.startPay((Activity)mContext, paramUrl, new IPayResultCallback() {
					@Override
					public void onPayResult(int resultCode, String signValue,
							String resultInfo) {
						if (SDKApi.PAY_SUCCESS == resultCode) {
							Log.e("xx", "signValue = " + signValue);
							if (null == signValue) {
								// 没有签名值，默认采用finish()，请根据需要修改
								Log.e("xx", "signValue is null ");
								//Toast.makeText((Activity)mContext, "没有签名值", Toast.LENGTH_SHORT)
										//.show();
								// //finish();
							}
							boolean flag = PayRequest.isLegalSign(signValue,appkey);
							if (flag) {
								Log.e("payexample", "islegalsign: true");
								//Toast.makeText((Activity)mContext, "支付成功", Toast.LENGTH_SHORT).show();
								// 合法签名值，支付成功，请添加支付成功后的业务逻辑
								
								onLenovoPurchaseSuccess();
								
							} else {
								Toast.makeText((Activity)mContext, "支付成功，但是验证签名失败",
										Toast.LENGTH_SHORT).show();
								// 非法签名值，默认采用finish()，请根据需要修改
							}
						} else if(SDKApi.PAY_CANCEL == resultCode){
							//Toast.makeText((Activity)mContext, "取消支付", Toast.LENGTH_SHORT).show();
							// 取消支付处理，默认采用finish()，请根据需要修改
							Log.e("fang", "return cancel");
							
							onLenovoPurchaseCancel();
							
						}else {
							//Toast.makeText((Activity)mContext, "支付失败", Toast.LENGTH_SHORT).show();
							// 计费失败处理，默认采用finish()，请根据需要修改
							Log.e("fang", "return Error");
							
							onLenovoPurchaseFail("");
						}
					}
				});
			}
		});
	}
	
	public static native void onLenovoPurchaseSuccess();
	
	public static native void onLenovoPurchaseCancel();
	
	public static native void onLenovoPurchaseFail(String error);
	
}