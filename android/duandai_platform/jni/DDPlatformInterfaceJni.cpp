#include "stdafx.h"
#include "platform/android/jni/JniHelper.h"
#include "IPlatform.h"

bool DDPlatformInit()
{
	CCLOG(__FUNCTION__);

	bool result = false;

	JniMethodInfo t;
	if (JniHelper::getStaticMethodInfo(t
		, "com/izhangxin/platform/duandai/DDPlatformInterface"
		, "DDInit"
		, "()Z"))
	{
		result = t.env->CallStaticBooleanMethod(t.classID, t.methodID);
		t.env->DeleteLocalRef(t.classID);

		return result;

	}else
	{
		CCAssert(false, "com.izhangxin.platform.duandai.DDPlatformInterface not valid!");
	}

	return result;
}

void DDPlatformPurchase(const char* order, Chest chest, const char* param, ...)
{
	CCLOG(__FUNCTION__);

	JniMethodInfo t;
	if (JniHelper::getStaticMethodInfo(t
		, "com/izhangxin/platform/duandai/DDPlatformInterface"
		, "DDPurchase"
		, "(Ljava/lang/String;Ljava/lang/String;I)V"))
	{
		jstring jstrOrder, jstrSerino;
		jstrOrder = t.env->NewStringUTF(order);
		jstrSerino = t.env->NewStringUTF(param);

		t.env->CallStaticVoidMethod(t.classID, t.methodID, jstrOrder, jstrSerino, chest.sale_money_);
		t.env->DeleteLocalRef(t.classID);
		t.env->DeleteLocalRef(jstrOrder);
		t.env->DeleteLocalRef(jstrSerino);

	}else
	{
		CCAssert(false, "com.izhangxin.platform.duandai.DDPlatformInterface not valid!");
	}
}

extern "C"
{
	/*
	 * Class:     com_izhangxin_platform_duandai_DDPlatformInterface
	 * Method:    onDDPurchaseSuccess
	 * Signature: ()V
	 */
	JNIEXPORT void JNICALL Java_com_izhangxin_platform_duandai_DDPlatformInterface_onDDPurchaseSuccess
	  (JNIEnv *env, jclass jcls)
	{
		CCLOG(__FUNCTION__);

		IPlatformEvent *event = IPlatformEvent::create(kIPlatformEventPaySmSSuccess, NULL);
		if(event)
			event->push();
	}

	JNIEXPORT void JNICALL Java_com_izhangxin_platform_duandai_DDPlatformInterface_onDDPurchaseSmSSuccess
		(JNIEnv *env, jclass jcls)
	{
		CCLOG(__FUNCTION__);

		IPlatformEvent *event = IPlatformEvent::create(kIPlatformEventPaySmSClient, NULL);
		if(event)
			event->push();
	}
	/*
	 * Class:     com_izhangxin_platform_duandai_DDPlatformInterface
	 * Method:    onDDPurchaseCancel
	 * Signature: ()V
	 */
	JNIEXPORT void JNICALL Java_com_izhangxin_platform_duandai_DDPlatformInterface_onDDPurchaseCancel
	  (JNIEnv *env, jclass jcls)
	{
		CCLOG(__FUNCTION__);

		IPlatformEvent *event = IPlatformEvent::create(kIPlatformEventPayResultCancel, NULL);
		if(event)
			event->push();
	}

	/*
	 * Class:     com_izhangxin_platform_duandai_DDPlatformInterface
	 * Method:    onDDPurchaseFail
	 * Signature: (Ljava/lang/String;)V
	 */
	JNIEXPORT void JNICALL Java_com_izhangxin_platform_duandai_DDPlatformInterface_onDDPurchaseFail
	  (JNIEnv *env, jclass jcls, jstring jerror)
	{
		CCLOG(__FUNCTION__);

		string error_msg = JniHelper::jstring2string(jerror);

		IPlatformEvent *event = IPlatformEvent::create(kIPlatformEventPayResultFail, &error_msg);
		if(event)
			event->pushInCurThread();
	}
}
