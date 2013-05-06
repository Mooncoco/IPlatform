#include "stdafx.h"
#include "platform/android/jni/JniHelper.h"
#include "IPlatform.h"


bool IAPPlatformInit()
{
	CCLOG(__FUNCTION__);

	bool result = false;

	JniMethodInfo t;
	if (JniHelper::getStaticMethodInfo(t
		, "com/izhangxin/platform/lenovo/LenovoPlatformInterface"
		, "LenovoInit"
		, "()Z"))
	{
		result = t.env->CallStaticBooleanMethod(t.classID, t.methodID);
		t.env->DeleteLocalRef(t.classID);

		return result;

	}else
	{
		CCAssert(false, "com.izhangxin.platform.iapppay.IAPPlatformInterface not valid!");
	}

	return result;
}

void LenovoPlatformPurchase(const char* order, Chest chest, const char* param, ...)
{
	CCLOG(__FUNCTION__);

	JniMethodInfo t;
	if (JniHelper::getStaticMethodInfo(t
		, "com/izhangxin/platform/lenovo/LenovoPlatformInterface"
		, "LenovoPurchase"
		, "(Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;)V"))
	{
		jstring jstrOrder, jstrProductName, jproductID;
		jstrOrder = t.env->NewStringUTF(order);
		jproductID = t.env->NewStringUTF(param);
		jstrProductName = t.env->NewStringUTF(chest.goods_name_.c_str());

		t.env->CallStaticVoidMethod(t.classID, t.methodID, jstrOrder, jstrProductName, chest.sale_money_, jproductID);
		t.env->DeleteLocalRef(t.classID);
		t.env->DeleteLocalRef(jstrOrder);
		t.env->DeleteLocalRef(jstrProductName);

	}else
	{
		CCAssert(false, "com.izhangxin.platform.lenovo.LenovoPlatformInterface not valid!");
	}
}

extern "C"
{
	/*
	 * Class:     com_izhangxin_platform_lenovo_LenovoPlatformInterface
	 * Method:    onLenovoPurchaseSuccess
	 * Signature: ()V
	 */
	JNIEXPORT void JNICALL Java_com_izhangxin_platform_lenovo_LenovoPlatformInterface_onLenovoPurchaseSuccess
	  (JNIEnv *env, jclass jcls)
	{
		CCLOG(__FUNCTION__);

		IPlatformEvent *event = IPlatformEvent::create(kIPlatformEventPayResultSuccess, NULL);
		if(event)
			event->push();
	}

	/*
	 * Class:     com_izhangxin_platform_lenovo_LenovoPlatformInterface
	 * Method:    onIAPPurchaseCancel
	 * Signature: ()V
	 */
	JNIEXPORT void JNICALL Java_com_izhangxin_platform_iapppay_IAPPlatformInterface_onIAPPurchaseCancel
	  (JNIEnv *env, jclass jcls)
	{
		CCLOG(__FUNCTION__);

		IPlatformEvent *event = IPlatformEvent::create(kIPlatformEventPayResultCancel, NULL);
		if(event)
			event->push();
	}

	/*
	 * Class:     com_izhangxin_platform_lenovo_LenovoPlatformInterface
	 * Method:    onIAPPurchaseFail
	 * Signature: (Ljava/lang/String;)V
	 */
	JNIEXPORT void JNICALL Java_com_izhangxin_platform_lenovo_LenovoPlatformInterface_onLenovoPurchaseFail
	  (JNIEnv *env, jclass jcls, jstring jerror)
	{
		CCLOG(__FUNCTION__);

		string error_msg = JniHelper::jstring2string(jerror);

		IPlatformEvent *event = IPlatformEvent::create(kIPlatformEventPayResultFail, &error_msg);
		if(event)
			event->pushInCurThread();
	}
}
