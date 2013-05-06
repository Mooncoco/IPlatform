#include "stdafx.h"
#include "platform/android/jni/JniHelper.h"
#include "IPlatform.h"


bool SohupayPlatformInit()
{
	CCLOG(__FUNCTION__);

	bool result = false;

	JniMethodInfo t;
	if (JniHelper::getStaticMethodInfo(t
		, "com/izhangxin/platform/sohupay/SohupayPlatformInterface"
		, "SohupayInit"
		, "()Z"))
	{
		result = t.env->CallStaticBooleanMethod(t.classID, t.methodID);
		t.env->DeleteLocalRef(t.classID);

		return result;

	}else
	{
		CCAssert(false, "com.izhangxin.platform.Sohupay.SohupayPlatformInterface not valid!");
	}

	return result;
}

void SohupayPlatformPurchase(const char* order, Chest chest, const char* cid, const char* orderTime, const char* payId, const char* pid, const char* smid, const char* sign)
{
	CCLOG(__FUNCTION__);

	JniMethodInfo t;
	if (JniHelper::getStaticMethodInfo(t
		, "com/izhangxin/platform/sohupay/SohupayPlatformInterface"
		, "SohupayPurchase"
		, "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"))
	{
		CCLOG("<--------------------->");
		jstring jstrCid, jstrName, jstrNameDesc, jstrOrderNo, jstrPayId, jstrOrderTime, jstrPid, jstrSmid, jstrSign;

		jstrCid = t.env->NewStringUTF(cid);
		jstrName = t.env->NewStringUTF(chest.goods_name_.c_str());
		jstrNameDesc = t.env->NewStringUTF(chest.desc_.c_str());
		jstrOrderNo = t.env->NewStringUTF(order);
		jstrOrderTime = t.env->NewStringUTF(orderTime);
		jstrPayId = t.env->NewStringUTF(payId);
		jstrPid = t.env->NewStringUTF(pid);
		jstrSmid = t.env->NewStringUTF(smid);
		jstrSign = t.env->NewStringUTF(sign);

		t.env->CallStaticVoidMethod(t.classID, t.methodID, jstrCid, jstrName, jstrNameDesc, jstrOrderNo, jstrOrderTime, jstrPayId, jstrPid, jstrSmid, jstrSign);
		t.env->DeleteLocalRef(t.classID);
		t.env->DeleteLocalRef(jstrCid);
		t.env->DeleteLocalRef(jstrName);
		t.env->DeleteLocalRef(jstrNameDesc);
		t.env->DeleteLocalRef(jstrOrderNo);
		t.env->DeleteLocalRef(jstrOrderTime);
		t.env->DeleteLocalRef(jstrPayId);
		t.env->DeleteLocalRef(jstrPid);
		t.env->DeleteLocalRef(jstrSmid);
		t.env->DeleteLocalRef(jstrSign);

	}else
	{
		CCAssert(false, "com.izhangxin.platform.Sohupaypay.SohupayPlatformInterface not valid!");
	}
}

extern "C"
{
	/*
	 * Class:     com_izhangxin_platform_sohupaypay_SohupayPlatformInterface
	 * Method:    onSohupayPurchaseSuccess
	 * Signature: ()V
	 */
	JNIEXPORT void JNICALL Java_com_izhangxin_platform_Sohupay_SohupayPlatformInterface_onSohupayPurchaseSuccess
	  (JNIEnv *env, jclass jcls)
	{
		CCLOG(__FUNCTION__);

		IPlatformEvent *event = IPlatformEvent::create(kIPlatformEventPayResultSuccess, NULL);
		if(event)
			event->push();
	}

	/*
	 * Class:     com_izhangxin_platform_sohupayp_SohupayPlatformInterface
	 * Method:    onSohupayPurchaseCancel
	 * Signature: ()V
	 */
	JNIEXPORT void JNICALL Java_com_izhangxin_platform_sohupay_SohupayPlatformInterface_onSohupayPurchaseCancel
	  (JNIEnv *env, jclass jcls)
	{
		CCLOG(__FUNCTION__);

		IPlatformEvent *event = IPlatformEvent::create(kIPlatformEventPayResultCancel, NULL);
		if(event)
			event->push();
	}

	/*
	 * Class:     com_izhangxin_platform_sohupay_SohupayPlatformInterface
	 * Method:    onSohupayPurchaseFail
	 * Signature: (Ljava/lang/String;)V
	 */
	JNIEXPORT void JNICALL Java_com_izhangxin_platform_sohupay_SohupayPlatformInterface_onSohupayPurchaseFail
	  (JNIEnv *env, jclass jcls, jstring jerror)
	{
		CCLOG(__FUNCTION__);
		/*
		IPlatformEvent *event = IPlatformEvent::create(KIPlatformEventPayProcess, NULL);
		if(event)
			event->push();
		*/
		string error_msg = JniHelper::jstring2string(jerror);

		IPlatformEvent *event = IPlatformEvent::create(kIPlatformEventPayResultFail, &error_msg);
		if(event)
			event->pushInCurThread();
	}
	/*
	 * Class:     com_izhangxin_platform_sohupay_SohupayPlatformInterface
	 * Method:    onSohupayPurchaseProcess
	 * Signature: (Ljava/lang/String;)V
	 */
	JNIEXPORT void JNICALL Java_com_izhangxin_platform_sohupay_SohupayPlatformInterface_onSohupayPurchaseProcess
		(JNIEnv *env, jclass jcls, jstring jerror)
	{
		CCLOG(__FUNCTION__);

		IPlatformEvent *event = IPlatformEvent::create(KIPlatformEventPayProcess, NULL);
		if(event)
			event->push();
	}
}
