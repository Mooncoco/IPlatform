package com.izhangxin.dx.android.handler;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;

import com.egame.webfee.EgameFee;
import com.egame.webfee.EgameFeeChannel;
import com.egame.webfee.EgameFeeResultListener;

public class IPlatformHandler
{
	public static IPlatformHandler instance = new IPlatformHandler();

	private static Context context;
	
	public static void setContext(Context context)
	{
		IPlatformHandler.context = context;
		
		EgameFee.init(context, new EgameFeeResultListener() {

				@Override
				public void egameFeeSucceed(int gameUserId, int feeMoney,
						EgameFeeChannel feeChannel) {
					System.out.println("egameFeeSucceed");
					onPayResult(0);
				}

				@Override
				public void egameFeeCancel() {
					System.out.println("egameFeeCancel");
					onPayResult(2);
				}

				@Override
				public void egameFeeFailed() {
					System.out.println("egameFeeFailed");
					onPayResult(1);
				}
			});
		
	}
	
	public static void  IPlatformPayInit()
	{
		
	}
	
	public static void IPlatformPay(String pOrder, String pProductName, String pProductDesc,
			String pProductSerino,float fProductPrice,float fProductOrignalPrice, int nProductCount)
	{
		System.out.println("order:"+pOrder+"money:"+fProductPrice);
		
		final int order_ = Integer.valueOf(pOrder);
		final int pay_money_ = (int)fProductPrice;
		
		((Activity) context).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				
				EgameFee.pay(order_, pay_money_);
				//onPayResult(2);
			}
		});
	}
	
	public static void IPlatformPayQuick(String pOrder, String pProductName, String pProductDesc,
			String pProductSerino,float fProductPrice,float fProductOrignalPrice, int nProductCount,int gameId)
	{
		System.out.println("order:"+pOrder+"money:"+fProductPrice +"gameId" + gameId);
		
		final int order_ = Integer.valueOf(pOrder);
		final int pay_money_ = (int)fProductPrice;
		
		((Activity) context).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				
				EgameFee.payBySms(order_, pay_money_,true);
			}
		});

		
		
		/*
		int order_ = Integer.valueOf(pOrder);
		int pay_money_ = (int)fProductPrice;
		
		String SENT_SMS_ACTION = "SENT_SMS_ACTION";
		Intent sentIntent = new Intent(SENT_SMS_ACTION);
		PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, sentIntent,
		        0);
		// register the Broadcast Receivers
		context.registerReceiver(new BroadcastReceiver() {
		    @Override
		    public void onReceive(Context _context, Intent _intent) {
		    	System.out.println("registerReceiver+++++++++code:"+getResultCode());
		        switch (getResultCode()) {
		        case Activity.RESULT_OK:
		        	onPayResult(3);
		        	//Toast.makeText(context,
		        //"短信发送成功", Toast.LENGTH_SHORT)
		        //.show();
		        break;
		        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
		        break;
		        case SmsManager.RESULT_ERROR_RADIO_OFF:
		        break;
		        case SmsManager.RESULT_ERROR_NULL_PDU:
		        break;
		        }
		    }
		}, new IntentFilter(SENT_SMS_ACTION));

		/*
		String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";
		// create the deilverIntent parameter
		Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);
		PendingIntent deliverPI = PendingIntent.getBroadcast(context, 0,
		       deliverIntent, 0);
		context.registerReceiver(new BroadcastReceiver() {
		   @Override
		   public void onReceive(Context _context, Intent _intent) {
			   System.out.println("receive_sms-------------------------------------------");
		       //Toast.makeText(context,
		  //"收信人已经成功接收", Toast.LENGTH_SHORT)
		 // .show();
		   }
		}, new IntentFilter(DELIVERED_SMS_ACTION));
		
		List<String> code_ = null;
		
		//根据不同的gameid调用不同的计费代码，
		if(gameId == 10)
			code_ = getDDZCode(pay_money_,order_);
		else if(gameId == 13)
			code_ = getERMJCode(pay_money_,order_);
		
		if(code_ == null || code_.size() < 2)
			return;
		
		SmsManager smsManager = SmsManager.getDefault();  
		
		List<String> divideContents = smsManager.divideMessage(code_.get(1));
		
		for (String text : divideContents) {  
			
			System.out.println("send===code===>" + code_.get(0) +","+ code_.get(1));
		    smsManager.sendTextMessage(code_.get(0), null, text, sentPI, null);    
		} 
		*/
		//EgameFee.paySMS(order_, pay_money_);
	}
	
	private static List<String> getDDZCode(int pay_money,int order) {
		
		List<String> codes_ = new ArrayList<String>();
		
		String			sendId = null;
		StringBuffer 	sendContent = null;
		
		switch(pay_money)
		{
			case 2:
				sendId = "10659811002";
				sendContent = new StringBuffer("0211C3203912035179369912035179300801MC099574000000000000000000000000");
				break;
			case 5:
				sendId = "10659811005";
				sendContent = new StringBuffer("0511C3203912035179369912035179300901MC099574000000000000000000000000");
				break;
			case 10:
				sendId = "10659811010";
				sendContent = new StringBuffer("1011C3203912035179369912035179301001MC099574000000000000000000000000");
				break;
			case 15:
				sendId = "10659811015";
				sendContent = new StringBuffer("1511C3203912035179369912035179301101MC099574000000000000000000000000");
				break;
			case 20:
				sendId = "10659811020";
				sendContent = new StringBuffer("2011C3203912035179369912035179301201MC099574000000000000000000000000");
				break;
			default:
				sendId = "10659811001";
				sendContent = new StringBuffer("0111C3203912035179369912035179300701MC099574000000000000000000000000");
				break;			
		}
		
		codes_.add(sendId);
		
		sendContent.replace(56, 63, String.format("%07x", order));
	
		codes_.add(sendContent.toString());
		
		// TODO Auto-generated method stub
		return codes_;
	}
	
	private static List<String> getERMJCode(int pay_money,int order) {
		
		List<String> codes_ = new ArrayList<String>();
		
		String			sendId = null;
		StringBuffer 	sendContent = null;
		
		switch(pay_money)
		{
			case 2:
				sendId = "10659811002";
				sendContent = new StringBuffer("0211C3203912035180174912035180100801MC099574000000000000000000000000");
				break;
			case 5:
				sendId = "10659811005";
				sendContent = new StringBuffer("0511C3203912035180174912035180100901MC099574000000000000000000000000");
				break;
			case 10:
				sendId = "10659811010";
				sendContent = new StringBuffer("1011C3203912035180174912035180101001MC099574000000000000000000000000");
				break;
			case 15:
				sendId = "10659811015";
				sendContent = new StringBuffer("1511C3203912035180174912035180101101MC099574000000000000000000000000");
				break;
			case 20:
				sendId = "10659811020";
				sendContent = new StringBuffer("2011C3203912035180174912035180101201MC099574000000000000000000000000");
				break;
			default:
				sendId = "10659811001";
				sendContent = new StringBuffer("0111C3203912035180174912035180100701MC099574000000000000000000000000");
				break;			
		}
		
		codes_.add(sendId);
		
		sendContent.replace(56, 63, String.format("%07x", order));
	
		codes_.add(sendContent.toString());
		
		// TODO Auto-generated method stub
		return codes_;
	}

	public static native void onPayResult(int status);
}