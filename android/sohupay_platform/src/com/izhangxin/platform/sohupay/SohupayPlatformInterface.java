package com.izhangxin.platform.sohupay;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.sohu.pay.Constants;
import com.sohu.pay.order.OrderInfo;
import com.sohu.pay.ui.StartSohuPayActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

public class SohupayPlatformInterface {

	private static String TAG = "sohupay_platform";

	private static Context mContext = null;
	
	private static ProgressDialog mProgress = null;
	
	public static SohupayPlatformInterface instance = null;

	public static void setContext(Context context) {
		mContext = context;
	}
	
	/**
	 * 初始化SDK
	 * @return
	 */
	public static boolean SohupayPlatformInit() {
		
		return true;
	}
	
	/**
	 * 调用Sohu SDK
	 * @param cid 渠道ID
	 * @param name 计费点名称
	 * @param nameDesc 计费点内容
	 * @param orderNo 订单号
	 * @param orderTime 订单生成时间
	 * @param payId 计费点ID
	 * @param pid 商品ID
	 * @param smid 合作方商户ID
	 * @param sign 订单信息签名
	 */
	public static void SohupayPurchase(final String cid, final String name, 
			final String nameDesc, final String orderNo, final String orderTime, final String payId, 
			final String pid, final String smid, final String sign) {
		
		System.out.println("::order:" + orderNo + "::orederTime:" + orderTime);
		
		//((Activity) mContext).runOnUiThread(new Runnable() {
			
			//public void run() {
				onSohupayPurchaseProcess();
				OrderInfo orderInfo = new OrderInfo();
				//设置订单信息
				setOrderInfo(orderInfo, cid, name, nameDesc, orderNo, orderTime, payId, pid, smid, sign);
				
				if (checkParams(orderInfo)) {	
					
					String strSign = generateSign(orderInfo);
					orderInfo.setSign(strSign);
					
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putSerializable("orderInfo", orderInfo);
					intent.putExtras(bundle);
					//设置启动跳转启动类
					intent.setClass(mContext, StartSohuPayActivity.class);
					//启动插件
					((Activity) mContext).startActivityForResult(intent, Constants.SOHUPAY_REQUEST_CODE);
				}else {
					System.out.println("OrderInfo is null!!!");
				}
			//}
		//});
	}
	/**
	 * 
	 * @param orderInfo
	 * @return
	 */
	private static String generateSign(final OrderInfo orderInfo) {
		StringBuilder sb = new StringBuilder();
		String cpid = orderInfo.getSmid().trim();

		// 合作方商户ID
		sb.append("smid=");
		sb.append(cpid.trim());

		// 渠道ID
		String cpcid = orderInfo.getCid().trim();
		sb.append("&cid=");
		sb.append(cpcid.trim());

		// 商品ID
		String cppid = orderInfo.getPid().trim();
		sb.append("&pid=");
		sb.append(cppid);

		// 计费点ID
		String payID = orderInfo.getPayID().trim();
		sb.append("&payID=");
		sb.append(payID);

		// 订单编号
		String orderno = orderInfo.getOrderno().trim();
		sb.append("&orderno=");
		sb.append(orderno);

		// 订单时间
		String ordertime = orderInfo.getOrdertime().trim();
		sb.append("&ordertime=");
		sb.append(ordertime);

		try {
			// 计费点名称
			String name = orderInfo.getName();
			sb.append("&name=");
			if (name != null && name.length() > 0) {
				sb.append(URLDecoder.decode(name, "utf-8"));
			}

			// 计费点描述
			String namedesc = orderInfo.getNamedesc();
			sb.append("&namedesc=");
			if (namedesc != null && namedesc.length() > 0) {
				sb.append(URLDecoder.decode(namedesc, "utf-8"));
			}
		} catch (UnsupportedEncodingException e) {
		}

		// 密钥， 从后台的商户信息中可以获取
		sb.append("&");
		sb.append("atkfqefag6#t5yitpefhhas3");
		
		Log.i("OrderInfo-before", sb.toString());
		// 生成MD5加密串
		String mysign = MD5Util.getMD5String(sb.toString());
		
		Log.i("OrderInfo-after", mysign);
		return mysign;
	}
	
	/**
	 * lephone系统使用到的取消dialog监听
	 */
	static class AlixOnCancelListener implements
			DialogInterface.OnCancelListener {
		Activity mcontext;

		AlixOnCancelListener(Activity context) {
			mcontext = context;
		}

		public void onCancel(DialogInterface dialog) {
			mcontext.onKeyDown(KeyEvent.KEYCODE_BACK, null);
		}
	}
	/**
	 * 关闭进度条
	 */
	void closeProgress() {
		try {
			if (mProgress != null) {
				mProgress.dismiss();
				mProgress = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 返回键监听事件
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			((Activity)mContext).finish();
			return true;
		}

		return false;
	}

	/**
	 * 验证订单参数合法性
	 */
	private static boolean checkParams(final OrderInfo orderInfo) {
		if (orderInfo == null || orderInfo.getCid() == null
				|| orderInfo.getOrderno() == null
				|| orderInfo.getOrdertime() == null
				|| orderInfo.getPayID() == null || orderInfo.getPid() == null
				|| orderInfo.getSmid() == null) {
			return false;
		}
		return true;
	}
	
	/**
	 * 设置订单信息
	 */
	private static void setOrderInfo(OrderInfo orderInfo, String cid, String name, String nameDesc, 
			String orderNo, String orderTime, String payId, String pid, String smid, String sign) {
		// 渠道ID
		orderInfo.setCid(cid.trim());
		// 计费点名称
		orderInfo.setName(name.trim());
		// 计费点内容
		orderInfo.setNamedesc(nameDesc.trim());
		// 订单号
		orderInfo.setOrderno(orderNo.trim());
		// 订单时间
		orderInfo.setOrdertime(orderTime.trim());
		// 计费点ID
		orderInfo.setPayID(payId.trim());
		// 商品ID
		orderInfo.setPid(pid.trim());
		// 合作方商户ID
		orderInfo.setSmid(smid.trim());
		//订单签名
		//orderInfo.setSign(sign.trim());
	}

	/**
	 * 获取插件的返回值，并提示用户。
	 */
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		// TODO Auto-generated method stub
		Log.i("gefy", "===================mainSohu #: ");
		Bundle extras = null;
		switch (requestCode) {
		case Constants.SOHUPAY_REQUEST_CODE:
			if (intent != null) {
				extras = intent.getExtras();
				if (extras != null) {
					
					String str = extras.getString( "result"); 

					Log.i("gefy", "mainSohu #: " + extras.getString("result"));
					
					System.out.println(resultCode);
									
					if (str == "0000") {
						onSohupayPurchaseSuccess();
					}
					else if (str == "8001")
					{
						onSohupayPurchaseCancel();
					}
					else
					{
						onSohupayPurchaseFail("");
					}
				}
			}
			break;
		default:
			break;
		}
	}

	public static native void onSohupayPurchaseSuccess();

	public static native void onSohupayPurchaseProcess();

	public static native void onSohupayPurchaseCancel();

	public static native void onSohupayPurchaseFail(String error);

}