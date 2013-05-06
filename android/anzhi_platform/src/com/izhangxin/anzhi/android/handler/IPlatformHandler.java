package com.izhangxin.anzhi.android.handler;

import java.math.BigDecimal;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.anzhi.pay.utils.AnzhiPayments;
import com.anzhi.pay.utils.PaymentsInterface;

public class IPlatformHandler implements PaymentsInterface
{
	public static IPlatformHandler instance = new IPlatformHandler();

	private static Context context;
	
	private static AnzhiPayments pay;
	
	public static void setContext(Context context)
	{
		IPlatformHandler.context = context;
	}
	
	public static void  IPlatformPayInit()
	{
		
	}
	
	public static void IPlatformPay(final String pOrder, String pProductName, final String pProductDesc,
			String pProductSerino,final float fProductPrice,float fProductOrignalPrice, int nProductCount)
	{
		System.out.println(":::pay:order:"+pOrder+"pProcuctDesc:"+ pProductDesc+"fpri"+fProductOrignalPrice+"nProCount:"+nProductCount);
		
		//int num = fProductPrice;
		// 对于金额的运算最好使用BigDecimal来操作
		
		((Activity) context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				
				BigDecimal decimal1 = new BigDecimal(String.valueOf(1)); // 购买数量
				BigDecimal decimal2 = new BigDecimal(fProductPrice); // 单价
				float price = decimal1.multiply(decimal2).floatValue();
				pay = AnzhiPayments.getInstance(context,
						//"FVpjHD2BLF4gobLo7xvSooCl",// app_key
						//"912EbJ1gVfB9QDwNpcvij9Iu" // app_secret
						"3F6PEQhbw4o3U1fGYfYgMh51",// app_key
						"F6VjRLYm3lc6hywK5p2U6Xir" //);// app_secret
						, "http://118.26.203.15:8080/pay/ws/payservice?wsdl");// 测试地址,该方法提供有重载方法，（测试时请保留该参数，应用正式上线后请务必删除该参数）

				pay.registerPaymentsCallBack(IPlatformHandler.instance);
//				pay.setAutoUpgrade(false);// 设置是否检测更新，默认自动更新
				// 注：AnzhiPayments提供了一些设置支付界面的方法，如果您要使用这些方法，请在调用pay方法之前使用有效。
				pay.pay(1, price, "安智提示：\n您准备支付 的金额为" + price + "元，" + pProductDesc, pOrder);
			}
		});
		
		
		 ((Activity) context).runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					
					onPayResult(2);
				}
			});
		 
		/*
		AnzhiPayments pay = AnzhiPayments.getInstance(context);
        pay.pay(nProductCount, fProductPrice, "您准备支付的金额为" + fProductPrice + "元，" + pProductDesc, pOrder,IPlatformHandler.instance);
        
       
        */
	}
	
	public static void onDestroy()
	{
		if (pay != null) {
			pay.unregisterPaymentsCallBack();
		}
	}
	
	@Override
    public void onPaymentsBegin() {
    	Log.e("test", "onPaymentsBegin");
    }

    @Override
    public void onPaymentsEnd() {
    	//onPayResult(2);
    	Log.e("test", "onPaymentsEnd");
    }

    @Override
    public void onPaymentsFail(int arg0, String arg1, String arg2) {
    	onPayResult(1);
        Log.e("test", "onPaymentsFail order= " + arg1 + " which= " + arg0);
    }

    @Override
    public void onPaymentsSuccess(int arg0, String arg1, float arg2) {
    	onPayResult(0);
        Log.e("test", "onPaymentsSuccess order= " + arg1 + " which= " + arg0);
    }
    
    public static native void onPayResult(int status);
}