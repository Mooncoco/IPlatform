#include "stdafx.h"
#include "platform/android/jni/JniHelper.h"
#include "IPlatform.h"


bool AlipayPlatformInit()
{
	CCLOG(__FUNCTION__);

	bool result = false;

	JniMethodInfo t;
	if (JniHelper::getStaticMethodInfo(t
		, "com/izhangxin/alipay/android/handler/AlipayPlatformInterface"
		, "AlipayPlatformPayInit"
		, "()Z"))
	{
		result = t.env->CallStaticBooleanMethod(t.classID, t.methodID);
		t.env->DeleteLocalRef(t.classID);

		return result;

	}else
	{
		CCAssert(false, "com.izhangxin.alipay.android.handler.AlipayPlatformInterface not valid!");
	}

	return result;
}

void AlipayPlatformPurchase(const char* order, Chest chest, const char* order_info, const char* sign)
{
	CCLOG(__FUNCTION__);

	JniMethodInfo t;
	if (JniHelper::getStaticMethodInfo(t
		, "com/izhangxin/alipay/android/handler/AlipayPlatformInterface"
		, "AlipayPlatformPurchase"
		, "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"))
	{
		jstring jstrOrder, jstrOrderInfo, jstrSign;

		jstrOrder = t.env->NewStringUTF(order);
		jstrOrderInfo = t.env->NewStringUTF(order_info);
		jstrSign = t.env->NewStringUTF(sign);

		t.env->CallStaticVoidMethod(t.classID, t.methodID, jstrOrder, jstrOrderInfo, jstrSign);
		t.env->DeleteLocalRef(t.classID);
		t.env->DeleteLocalRef(jstrOrder);
		t.env->DeleteLocalRef(jstrOrderInfo);
		t.env->DeleteLocalRef(jstrSign);

	}else
	{
		CCAssert(false, "com.izhangxin.alipay.android.handler.AlipayPlatformInterface not valid!");
	}
}

extern "C"
{
	/*
	 * Class:     com_izhangxin_platform_alipay_AlipayPlatformInterface
	 * Method:    onAlipayPurchaseSuccess
	 * Signature: ()V
	 */
	JNIEXPORT void JNICALL Java_com_izhangxin_alipay_android_handler_AlipayPlatformInterface_onAlipayPurchaseSuccess
	  (JNIEnv *env, jclass jcls)
	{
		CCLOG(__FUNCTION__);

		IPlatformEvent *event = IPlatformEvent::create(kIPlatformEventPayResultSuccess, NULL);
		if(event)
			event->push();
	}

	/*
	 * Class:     com_izhangxin_platform_alipay_AlipayPlatformInterface
	 * Method:    onAlipayPurchaseCancel
	 * Signature: ()V
	 */
	JNIEXPORT void JNICALL Java_com_izhangxin_alipay_android_handler_AlipayPlatformInterface_onAlipayPurchaseCancel
	  (JNIEnv *env, jclass jcls)
	{
		CCLOG(__FUNCTION__);

		IPlatformEvent *event = IPlatformEvent::create(kIPlatformEventPayResultCancel, NULL);
		if(event)
			event->push();
	}

	/*
	 * Class:     com_izhangxin_platform_alipay_AlipayPlatformInterface
	 * Method:    onAlipayPurchaseFail
	 * Signature: (Ljava/lang/String;)V
	 */
	JNIEXPORT void JNICALL Java_com_izhangxin_alipay_android_handler_AlipayPlatformInterface_onAlipayPurchaseFail
	  (JNIEnv *env, jclass jcls, jstring jerror)
	{
		CCLOG(__FUNCTION__);

		string error_msg = JniHelper::jstring2string(jerror);

		IPlatformEvent *event = IPlatformEvent::create(kIPlatformEventPayResultFail, &error_msg);
		if(event)
			event->pushInCurThread();
	}
}
