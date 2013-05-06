#include "stdafx.h"
#include "platform/android/jni/JniHelper.h"
#include "IPlatform.h"

bool Mo9PlatformInit()
{
	CCLOG(__FUNCTION__);

	bool result = false;

	JniMethodInfo t;
	if (JniHelper::getStaticMethodInfo(t
		, "com/izhangxin/platform/mo9/Mo9PlatformInterface"
		, "Mo9Init"
		, "()Z"))
	{
		result = t.env->CallStaticBooleanMethod(t.classID, t.methodID);
		t.env->DeleteLocalRef(t.classID);

		return result;

	}else
	{
		CCAssert(false, "com.izhangxin.platform.mo9.Mo9PlatformInterface not valid!");
	}

	return result;
}

void Mo9PlatformPurchase(const char* order, Chest chest, const char* param, ...)
{
	CCLOG(__FUNCTION__);

	JniMethodInfo t;
	if (JniHelper::getStaticMethodInfo(t
		, "com/izhangxin/platform/mo9/Mo9PlatformInterface"
		, "Mo9Purchase"
		, "(Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;)V"))
	{
		jstring jstrOrder, jstrProductName, juid;
		jstrOrder = t.env->NewStringUTF(order);
		juid = t.env->NewStringUTF(param);
		jstrProductName = t.env->NewStringUTF(chest.goods_name_.c_str());

		t.env->CallStaticVoidMethod(t.classID, t.methodID, jstrOrder, jstrProductName, chest.sale_money_, juid);
		t.env->DeleteLocalRef(t.classID);
		t.env->DeleteLocalRef(jstrOrder);
		t.env->DeleteLocalRef(jstrProductName);

	}else
	{
		CCAssert(false, "com.izhangxin.platform.mo9.Mo9PlatformInterface not valid!");
	}
}

extern "C"
{
	/*
	 * Class:     com_izhangxin_platform_mo9_Mo9PlatformInterface
	 * Method:    onMo9PurchaseSuccess
	 * Signature: ()V
	 */
	JNIEXPORT void JNICALL Java_com_izhangxin_platform_mo9_Mo9PlatformInterface_onMo9PurchaseSuccess
	  (JNIEnv *env, jclass jcls)
	{
		CCLOG(__FUNCTION__);

		IPlatformEvent *event = IPlatformEvent::create(kIPlatformEventPayResultSuccess, NULL);
		if(event)
			event->push();
	}

	/*
	 * Class:     com_izhangxin_platform_mo9_Mo9PlatformInterface
	 * Method:    onMo9PurchaseCancel
	 * Signature: ()V
	 */
	JNIEXPORT void JNICALL Java_com_izhangxin_platform_mo9_Mo9PlatformInterface_onMo9PurchaseCancel
	  (JNIEnv *env, jclass jcls)
	{
		CCLOG(__FUNCTION__);

		IPlatformEvent *event = IPlatformEvent::create(kIPlatformEventPayResultCancel, NULL);
		if(event)
			event->push();
	}

	/*
	 * Class:     com_izhangxin_platform_mo9_Mo9PlatformInterface
	 * Method:    onMo9PurchaseFail
	 * Signature: (Ljava/lang/String;)V
	 */
	JNIEXPORT void JNICALL Java_com_izhangxin_platform_mo9_Mo9PlatformInterface_onMo9PurchaseFail
	  (JNIEnv *env, jclass jcls, jstring jerror)
	{
		CCLOG(__FUNCTION__);

		string error_msg = JniHelper::jstring2string(jerror);

		IPlatformEvent *event = IPlatformEvent::create(kIPlatformEventPayResultFail, &error_msg);
		if(event)
			event->pushInCurThread();
	}
}
