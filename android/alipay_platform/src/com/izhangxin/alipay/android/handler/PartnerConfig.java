package com.izhangxin.alipay.android.handler;

public class PartnerConfig {

	// 合作商户ID。用签约支付宝账号登录ms.alipay.com后，在账户信息页面获取。
	public static final String PARTNER = "2088901165876504";
	// 商户收款的支付宝账号
	public static final String SELLER = "2088901165876504";
	// 商户（RSA）私钥
	public static final String RSA_PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMhWHTMNhoxt+QjhalEwrbNnKEMyD5ehMlflLFIyLhMDzWzcJuWvNzUHDFCjbZBezvV5Re0oYZgc6lC6z1579P+qTEPd15LcV/VnnNIQYPCdZZ/HZhh0pTFqOgc54DvbhqxAGtjDfxvRuoQ9z8G8SKawJiTVihFAfSdEYr1zvIIlAgMBAAECgYB8+RNgDLj5k4T+IDiFi3tgZiK8wFbvvTQ9lo4ufHU3kXCtzyxIriQfZVUuXLT1kd9DTLLtffpbvPYDBHZmN6eeV0Z9BK4rAzdMkTEIlTNQZJ6RsdnXlB2D8DGcr2m+harUEOsNLPEULa1CVS93iytkhs/a5IOFjHMpj/uolyQnZQJBAOhnh9J2V2HhDVAgsp4S24v5EuKDSPP7Xvmw6nASFq0UHmyHSi3XSolbCqy52ldZaUkH5YsisDtNIG5eECw5vG8CQQDcrRjMjEvk80EUudWBi/uCB5o10oCRJM2J15xb9WT5SgK92OlpYIBen+Il53F39uZgG90EHn3X538cd9PHKJyrAkEA1QvDeO2/YRzcBW5UD5Cd7ozgAQH06KSxBhhmcWJDo1pkIX1MRQWtcIkjGgAt5hEy8R3qEUg1YcdsSYC1NrWexQJBAJwadqh7tlRHH4qhyr7RXyEkb5oTRo9mEV+o/hqvBLaz/Cx//N1mPmixgTB/AG10YpMqIrZ/O1Y3lFt3CICh4fECQGcTrxUUQ0NLdDD0u/Fc+UtSL5WszfBNDSakPv9dHF/1tm7eHzq5ASBxl38P7jKR7WERKQhkTSPKSxJ4zppjHHs=";
	// 支付宝（RSA）公钥 用签约支付宝账号登录ms.alipay.com后，在密钥管理页面获取。
	public static final String RSA_ALIPAY_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDdj8hFu/dP1fgz/xVOU+eqnwuInO2km8alkb0Y 6w1j8EDFKnEihewGCrLZR82ueXJ356UJwwadjEg/ZEaVsqN4G9/IkHvjDmJqBWD9T2uO4Gp8Yo/l EiH+Gkm1rsnWzotJ33IOZJvQ1IehK0hPrdvCoDbk1BWvL9fkspY4UeTdtwIDAQAB";
	// 支付宝安全支付服务apk的名称，必须与assets目录下的apk名称一致
	public static final String ALIPAY_PLUGIN_NAME = "20121018133442msp.apk";
	
	public static final String PAY_RESULT_URL = "http://t.mall.hiigame.com/alipay/notify";

}
