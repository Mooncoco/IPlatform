package com.izhangxin.platform.mo9;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.mokredit.payment.Md5Encrypt;
import com.mokredit.payment.MktPayment;
import com.mokredit.payment.MktPluginSetting;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import android.app.Activity;;

public class Mo9PlatformInterface
{
	private static String TAG = "mo9_platform";

	private static Context mContext = null;

	public static void setContext(Context con) { mContext = con;}

	public static boolean Mo9Init()
	{
		Log.i(TAG, "MoInit");

		return true;
	}

	public static void Mo9Purchase(String order, String productName, int price, String uid)
	{
		Log.i(TAG, "MoPurchase order:" + order);
		
		/**拼接支付请求URI*/
		String uri = loadPaymentURLWithParams(order, productName, price, uid);
		/**创建一个mokreidt插件配置项和Intent，并启动SDK的支付对话框.*/
		MktPluginSetting pluginSetting= new MktPluginSetting (uri);
		Intent intent=new Intent();
		intent.setClass(mContext, MktPayment.class);
		intent.putExtra("mokredit_android", pluginSetting);
		/***
		 * 开始支付流程
		 */
//		startActivity(intent);
		/**
		 * 开启同步支付流程，你可再onActivityResult(int requestCode, int resultCode, Intent data)处理同步支付结果.
		 */
		((Activity)mContext).startActivityForResult(intent, 100);
	}
	
	/**
	 * 从你的服务器端加载支付URL.
	 *    为了避免将mo9.com分配给你的签名key存储在android而导致的密钥泄露风险，我们强烈建议将key存储在你的服务器。
	 * 服务端完成对支付URL签名后，再回传给Android客户端加载解析.
	 * Notice:
	 *    该实例方法为逐行为你注解每个接口参数的意义，及取值范围。接口的详细描述可参阅文档《mo9手机开发集成文档.pdf》
	 * @return 签名后的支付请求URL地址
	 */
	private static String loadPaymentURLWithParams(String order, String productName, int price, String uid)
	{	
		/**
		 * 支付请求地址：
		 *  我们一共提国际服务器,国际服务器沙箱环境，中国服务器，中国服务器沙箱环境四套环境，具体ＵＲＬ地址如下：
		 *  https://www.mo9.com/gateway/mobile.shtml?m=mobile
		 *  https://sandbox.mo9.com/gateway/mobile.shtml?m=mobile
		 *  https://www.mo9.com.cn/gateway/mobile.shtml?m=mobile
		 *  https://sandbox.mo9.com.cn/gateway/mobile.shtml?m=mobile
		 */
		//String pay_uri="https://sandbox.mo9.com.cn/gateway/mobile.shtml?m=mobile";
		String pay_uri = "https://www.mo9.com.cn/gateway/mobile.shtml?m=mobile";

		Map<String,String> payParams = new HashMap<String,String>();
		/**商户接入账号，你可以使用该账号登陆mo9后台查看支付订单*/
		payParams.put("pay_to_email", mContext.getString(R.string.app_email));
		/**商户接入点应用ID,你可登录mo9.com/merchant,mo9.com.cn/merchant,sandbox.mo9.com/merchant,sandbox.mo9.com.cn/merchant创建应用，系统将自定为你分配app_id*/
		payParams.put("app_id", mContext.getString(R.string.app_id));
		/**支付接口版本号，直接填写2即可*/
		payParams.put("version", mContext.getString(R.string.app_version));	
		/***支付回调回调地址.*/
		payParams.put("notify_url", mContext.getString(R.string.app_notify));
		/**
		 * 请求凭证，要求每一次支付请求的invoice参数均不相同，mo9系统会放弃处理已经存在的invoice，
		 * 以避免重复处理支付请求。我们建议你使用你系统的交易订单号作为invoice参数值.
		 */
		payParams.put("invoice", order);
		/**
		 * 商家系统中唯一的用户id
		 * mo9建议使用商户系统中的用户表ID列，或者其他具有唯一性约束的数据列(例如登陆名，手机号码等)填充该字段。
		 * 如果你需要在mo9系统中接入多款游戏，或同一款游戏中因为分服(中国服务器,美国服务器等)，分区(游戏一区,二区等)
		 * 业务导致存在多个不同数据库源。请结合数据库源生成唯一的payer_id,以避免来自不同服务器的payerId发生重复。
		 * 你也可以使用手机终端的IMEI,imsi等属性作为你唯一的PayerID标识.
		 */
		payParams.put("payer_id", uid);
		/**
		 * 用户所在的所有地区的国家缩写，例如中国为“CN”,美国“US”.
		 * 如果你的系统中已经保存了用户的所在地信息则可以直接使用，如果你的系统中没有用户所在地信息，则建议你按如下
		 * 优先级设置用户归属地信息：
		 * 1.使用手机终端SIM卡所在的归属地.TelephonyManager.getSimCountryIso()
		 * 2.使用手机终端所在的IP地址归属地.
		 * 3.使用手机操作系统的Local信息.Context.getResources().getConfiguration().locale.getCountry()
		 * 4.根据你的游戏运营情况，自定义归属地信息。例如你的游戏百分之九十都是中国用户，则可以直接设置为“CN”。
		 */
		payParams.put("lc", "CN");
		/***交易金额*/
		payParams.put("amount", String.valueOf(price));
		/**交易的货币类型*/
		payParams.put("currency", "CNY");
		/***交易的商品名称*/
		payParams.put("item_name", productName);
		
		/**
		 * 商户接入私钥
		 * 商户登陆https://sandbox.mokredit.com/merchant,既可以在商户Setting菜单下看到你的私钥信息。
		 * 请勿为了开发方便，将该key放置在客户端，以免key泄露导致损失.
		 */
		String privateKey=mContext.getString(R.string.app_key);
		
		/**
		 * com.mokredit.payment.Md5Encrypt位于我们提供的mokredit.jar文件中.
		 * 如果你不希望自己维护给予MD5的签名算法，可以导入mokredit.jar调用改方法，完成签名操作.
		 */
		String sign  = Md5Encrypt.sign(payParams, privateKey);
		payParams.put("sign", sign);
		
		/***
		 * 拼接请求参数,请在提交支付请求时使用UTF-8编码，以免item_name等包含汉字或特殊字符的属性不能正确识别.
		 */
		StringBuffer queryStr = new StringBuffer();
		Set<String> keys = payParams.keySet();
		for(String key:keys)
		{
			//将请求参数进行URL编码
			String value=null;
			try
			{
				value = URLEncoder.encode((String)payParams.get(key), "UTF-8");
			} catch (UnsupportedEncodingException e)
			{
				throw new IllegalArgumentException("封装支付请求URL失败.	",e);
			}
			queryStr.append("&"+key+"="+value);
		}
		
		Log.i(TAG,"url: "+pay_uri+"&"+queryStr);
		
		return pay_uri+"&"+queryStr;
	}
	
	public static native void onMo9PurchaseSuccess();
	
	public static native void onMo9PurchaseCancel();
	
	public static native void onMo9PurchaseFail(String error);
}