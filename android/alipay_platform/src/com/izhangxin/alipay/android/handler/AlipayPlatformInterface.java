package com.izhangxin.alipay.android.handler;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;


public class AlipayPlatformInterface
{
	static String TAG = "AlipayPlatformHandler";
	
	private static Context context;
	
	private static ProgressDialog mProgress = null;
	
	public static AlipayPlatformInterface instance = null;
	
	public static AlipayPlatformInterface getInstance()
	{
		if(instance == null)
		{
			return new AlipayPlatformInterface();
		}
		else
		{
			return instance;
		}
	}
	
	public static void setContext(Context context)
	{
		AlipayPlatformInterface.context = context;
	}
	
	public static void  AlipayPlatformPayInit()
	{
		
	}
	
	public static void AlipayPlatformPurchase(final String pOrder, final String order_info, final String sign)
	{
		System.out.println(":::pay:order:" + pOrder + ":jstrOrderInfo:" + order_info + ":jsSign" + sign);

        ((Activity) context).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				
				//onPayResult(2);
				
				//
				// check to see if the MobileSecurePay is already installed.
				// 检测安全支付服务是否安装
				MobileSecurePayHelper mspHelper = new MobileSecurePayHelper(context);
				boolean isMobile_spExist = mspHelper.detectMobile_sp();
				if (!isMobile_spExist)
					return;
				else
				{
					closeProgress();
				}
				// check some info.
				// 检测配置信息
				if (!checkInfo()) {
					BaseHelper.showDialog((Activity)context,"提示","缺少partner或者seller，请在src/com/izhangxin/zfb/android/handler/PartnerConfig.java中增加。",R.drawable.infoicon);
					return;
				}

				// start pay for this order.
				// 根据订单信息开始进行支付
				try {
					// prepare the order info.
					// 准备订单信息
					//String orderInfo = getOrderInfo(pOrder,pProductName,pProductDesc,"0.01");//String.valueOf(fProductPrice));
					// 这里根据签名方式对订单信息进行签名
					String signType = getSignType();
					// 对签名进行编码
					String strsign = URLEncoder.encode(sign);
					Log.v("SIGN",strsign);
					// 组装好参数
					String info = order_info + "&sign=" + "\"" + strsign + "\"" + "&" + signType;
					Log.v("orderInfo:", info);
					// start the pay.
					// 调用pay方法进行支付\
					MobileSecurePayer msp = new MobileSecurePayer();
					boolean bRet = msp.pay(info, mHandler, AlixId.RQF_PAY, (Activity)context);

					if (bRet) {
						// show the progress bar to indicate that we have started
						// paying.
						// 显示“正在支付”进度条
						closeProgress();
						mProgress = BaseHelper.showProgress(context, null, "正在支付", false,
								true);
					} else
						;
				} catch (Exception ex) {
					Toast.makeText((Activity)context, R.string.remote_call_failed,
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	/**
	 * get the selected order info for pay. 获取商品订单信息
	 * 
	 * @param position
	 *            商品在列表中的位置
	 * @return
	 */
	
	/*static String getOrderInfo(String order,String subject,String body,String total_fee) {
		
		String strOrderInfo = "partner=" + "\"" + PartnerConfig.PARTNER + "\"";
		strOrderInfo += "&";
		strOrderInfo += "seller=" + "\"" + PartnerConfig.SELLER + "\"";
		strOrderInfo += "&";
		strOrderInfo += "out_trade_no=" + "\"" + order + "\"";
		strOrderInfo += "&";
		strOrderInfo += "subject=" + "\"" + subject	+ "\"";
		strOrderInfo += "&";
		strOrderInfo += "body=" + "\"" + body + "\"";
		strOrderInfo += "&";
		strOrderInfo += "total_fee=" + "\"" + total_fee + "\"";
		strOrderInfo += "&";
		strOrderInfo += "notify_url=" + "\"" + PartnerConfig.PAY_RESULT_URL + "\"";
		
		return strOrderInfo;
	}*/

	/**
	 * get the out_trade_no for an order. 获取外部订单号
	 * 
	 * @return
	 */
	String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss");
		Date date = new Date();
		String strKey = format.format(date);

		java.util.Random r = new java.util.Random();
		strKey = strKey + r.nextInt();
		strKey = strKey.substring(0, 15);
		return strKey;
	}

	//
	//
	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param signType
	 *            签名方式
	 * @param content
	 *            待签名订单信息
	 * @return
	 */
	/*static String sign(String signType, String content) {
		return Rsa.sign(content, PartnerConfig.RSA_PRIVATE);
	}*/
	
	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 * @return
	 */
	static String getSignType() {
		String getSignType = "sign_type=" + "\"" + "RSA" + "\"";
		return getSignType;
	}

	/**
	 * get the char set we use. 获取字符集
	 * 
	 * @return
	 */
	String getCharset() {
		String charset = "charset=" + "\"" + "utf-8" + "\"";
		return charset;
	}

	/**
	 * check some info.the partner,seller etc. 检测配置信息
	 * partnerid商户id，seller收款帐号不能为空
	 * 
	 * @return
	 */
	private static boolean checkInfo() {
		String partner = PartnerConfig.PARTNER;
		String seller = PartnerConfig.SELLER;
		if (partner == null || partner.length() <= 0 || seller == null
				|| seller.length() <= 0)
			return false;

		return true;
	}
	
	//
	// the handler use to receive the pay result.
	// 这里接收支付结果，支付宝手机端同步通知
	private static Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			try {
				String strRet = (String) msg.obj;

				Log.e(TAG, strRet);	// strRet范例：resultStatus={9000};memo={};result={partner="2088201564809153"&seller="2088201564809153"&out_trade_no="050917083121576"&subject="123456"&body="2010新款NIKE 耐克902第三代板鞋 耐克男女鞋 386201 白红"&total_fee="0.01"&notify_url="http://notify.java.jpxx.org/index.jsp"&success="true"&sign_type="RSA"&sign="d9pdkfy75G997NiPS1yZoYNCmtRbdOP0usZIMmKCCMVqbSG1P44ohvqMYRztrB6ErgEecIiPj9UldV5nSy9CrBVjV54rBGoT6VSUF/ufjJeCSuL510JwaRpHtRPeURS1LXnSrbwtdkDOktXubQKnIMg2W0PreT1mRXDSaeEECzc="}
				switch (msg.what) {
				case AlixId.RQF_PAY: {
					//
					closeProgress();

					BaseHelper.log(TAG, strRet);

					// 处理交易结果
					try {
						// 获取交易状态码，具体状态代码请参看文档
						String tradeStatus = "resultStatus={";
						int imemoStart = strRet.indexOf("resultStatus=");
						imemoStart += tradeStatus.length();
						int imemoEnd = strRet.indexOf("};memo=");
						tradeStatus = strRet.substring(imemoStart, imemoEnd);
						
						//先验签通知
						ResultChecker resultChecker = new ResultChecker(strRet);
						int retVal = resultChecker.checkSign();
						// 验签失败
						if (retVal == ResultChecker.RESULT_CHECK_SIGN_FAILED) {
							BaseHelper.showDialog(
									(Activity)context,
									"提示",
									context.getResources().getString(
											R.string.check_sign_failed),
									android.R.drawable.ic_dialog_alert);
						} else {// 验签成功。验签成功后再判断交易状态码
							if(tradeStatus.equals("9000")) {//判断交易状态码，只有9000表示交易成功
								onAlipayPurchaseSuccess();
								//onPayResult(0);
								//BaseHelper.showDialog((Activity)context, "提示","支付成功。交易状态码："+tradeStatus, R.drawable.infoicon);
							}
							else if(tradeStatus.equals("6001")) {
							/*BaseHelper.showDialog((Activity)context, "提示", "支付失败。交易状态码:"
									+ tradeStatus, R.drawable.infoicon);*/
								onAlipayPurchaseCancle();
							}
							else {
								onAlipayPurchaseFail("");
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
						BaseHelper.showDialog((Activity)context, "提示", strRet,
								R.drawable.infoicon);
					}
				}
					break;
				}

				super.handleMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	//
	//
	/**
	 * the OnCancelListener for lephone platform. lephone系统使用到的取消dialog监听
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

	//
	// close the progress bar
	// 关闭进度框
	static void closeProgress() {
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
			BaseHelper.log(TAG, "onKeyDown back");

			//this.finish();
			return true;
		}

		return false;
	}

	//
	public void onDestroy() {
		//super.onDestroy();
		Log.v(TAG, "onDestroy");

		try {
			mProgress.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    //public static native void onPayResult(int status);
    public static native void onAlipayPurchaseSuccess();
    public static native void onAlipayPurchaseCancle();
    public static native void onAlipayPurchaseFail(String error);
}