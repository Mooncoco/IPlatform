#include "stdafx.h"
#include "platform/android/jni/JniHelper.h"
#include "IPlatform.h"
#include "game_bdohttp_proto.h"
#include "data_module.h"

std::string strHeadFacePath = "";
CCNode *pSender = NULL;
SEL_CallFuncN callFunc = NULL;

bool IPlatformInit()
{
	CCLOG(__FUNCTION__);

	bool result = false;

	JniMethodInfo t;
	if (JniHelper::getStaticMethodInfo(t
		, "com/izhangxin/platform/downjoy/AndroidPlatformInterface"
		, "DJInit"
		, "()Z"))
	{
		result = t.env->CallStaticBooleanMethod(t.classID, t.methodID);
		t.env->DeleteLocalRef(t.classID);

		return result;

	}else
	{
		CCAssert(false, "com.izhangxin.platform.downjoy.AndroidPlatformInterface not valid!");
	}

	return result;
}

bool IPlatformInitWithParams(const char* app_id, const char* app_key, const char* pay_id) { return true; }

bool IPlatformIsLogined() { return false; }

void IPlatformLogin()
{
	CCLOG(__FUNCTION__);

	JniMethodInfo t;
	if (JniHelper::getStaticMethodInfo(t
		, "com/izhangxin/platform/downjoy/AndroidPlatformInterface"
		, "DJLogin"
		, "()V"))
	{
		t.env->CallStaticVoidMethod(t.classID, t.methodID);
		t.env->DeleteLocalRef(t.classID);

	}else
	{
		CCAssert(false, "com.izhangxin.platform.downjoy.AndroidPlatformInterface not valid!");
	}
}

void IPlatformLoginWithCallBack(CCNode* node, SEL_CallFuncN call)
{
	pSender = node;
	callFunc = call;

	IPlatformLogin();
}

void IPlatformLogout()
{
	CCLOG(__FUNCTION__);

	JniMethodInfo t;
	if (JniHelper::getStaticMethodInfo(t
		, "com/izhangxin/platform/downjoy/AndroidPlatformInterface"
		, "DJLogout"
		, "()V"))
	{
		t.env->CallStaticVoidMethod(t.classID, t.methodID);
		t.env->DeleteLocalRef(t.classID);

	}else
	{
		CCAssert(false, "com.izhangxin.platform.downjoy.AndroidPlatformInterface not valid!");
	}
}

void IPlatformClear()
{
	IPlatformLogout();
}

void IPlatformSwitchAccount()
{
	IPlatformLogout();
}

void IPlatformEnterPlatformCenter()
{
	CCLOG(__FUNCTION__);

	JniMethodInfo t;
	if (JniHelper::getStaticMethodInfo(t
		, "com/izhangxin/platform/downjoy/AndroidPlatformInterface"
		, "DJEnterCenter"
		, "()V"))
	{
		t.env->CallStaticVoidMethod(t.classID, t.methodID);
		t.env->DeleteLocalRef(t.classID);

	}else
	{
		CCAssert(false, "com.izhangxin.platform.downjoy.AndroidPlatformInterface not valid!");
	}
}

// void IPlatformPay(const char* order, Chest chest)
// {
// 	CCLOG(__FUNCTION__);

// 	JniMethodInfo t;
// 	if (JniHelper::getStaticMethodInfo(t
// 		, "com/izhangxin/platform/downjoy/AndroidPlatformInterface"
// 		, "DJPay"
// 		, "(Ljava/lang/String;Ljava/lang/String;)V"))
// 	{
// 		jstring jstrOrder, jproductID;
// 		jstrOrder = t.env->NewStringUTF(order);
// 		jproductID = t.env->NewStringUTF(chest.serialno_.c_str());

// 		t.env->CallStaticVoidMethod(t.classID, t.methodID, jstrOrder, jproductID);
// 		t.env->DeleteLocalRef(t.classID);
// 		t.env->DeleteLocalRef(jstrOrder);
// 		t.env->DeleteLocalRef(jproductID);

// 	}else
// 	{
// 		CCAssert(false, "com.izhangxin.platform.downjoy.AndroidPlatformInterface not valid!");
// 	}
// }

void IPlatformPurchase(const char* order, const char* productName, int price, const char *param, ...)
{
	CCLOG(__FUNCTION__);

	JniMethodInfo t;
	if (JniHelper::getStaticMethodInfo(t
		, "com/izhangxin/platform/downjoy/AndroidPlatformInterface"
		, "DJPurchase"
		, "(Ljava/lang/String;Ljava/lang/String;I)V"))
	{
		jstring jstrOrder, jstrProductName;
		jstrOrder = t.env->NewStringUTF(order);
		jstrProductName = t.env->NewStringUTF(productName);

		t.env->CallStaticVoidMethod(t.classID, t.methodID, jstrOrder, jstrProductName, price);
		t.env->DeleteLocalRef(t.classID);
		t.env->DeleteLocalRef(jstrOrder);
		t.env->DeleteLocalRef(jstrProductName);

	}else
	{
		CCAssert(false, "com.izhangxin.platform.downjoy.AndroidPlatformInterface not valid!");
	}
}

void IPlatformInitHeadFace(){}

CCTexture2D* IPlatformGetHeadFace()
{
	if(strHeadFacePath.empty())
		return NULL;

	return CCTextureCache::sharedTextureCache()->addImage(strHeadFacePath.c_str());
}

void IPlatformShareWithScreenShot(const char* pText) {}

void IPlatformShareWithImageUrl(const char* text, const char* path) {}

void IPlatformUploadHeadFace(const char* uploadUrl, const char* filePath, const char* guid) {}

bool IPlatformExternalStorageState() { return true; }

extern "C"
{
	/*
	 * Class:     com_izhangxin_platform_downjoy_AndroidPlatformInterface
	 * Method:    onDJLoginSuccess
	 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
	 */
	JNIEXPORT void JNICALL Java_com_izhangxin_platform_downjoy_AndroidPlatformInterface_onDJLoginSuccess
	  (JNIEnv *env, jclass jcls, jstring jappid, jstring juid, jstring jtoken, jstring jnickname)
	{
		CCLOG(__FUNCTION__);

		IPlatformEvent *event = IPlatformEvent::create(kIPlatformEventLoginWillLoad, NULL);
		if(event)
			event->push();

		string strURL = GameBDOHttpProto::shareGameBDOHttpProto()->getUrlPre();
		strURL += "d/authen";

		string strNickname = JniHelper::jstring2string(jnickname);
		if(strNickname.empty())
			strNickname = DataModule::GetInstancePtr()->device_data().phone_name_;

		char buff[256];
		sprintf(buff, "appid=%s&uid=%s&session=%s&pn=%s&imei=%s&name=%s",
				JniHelper::jstring2string(jappid).c_str(),
				JniHelper::jstring2string(juid).c_str(),
				JniHelper::jstring2string(jtoken).c_str(),
				DataModule::GetInstancePtr()->device_data().game_packet_name_.c_str(),
				DataModule::GetInstancePtr()->device_data().phone_imei_.c_str(),
				strNickname.c_str());

		string strContent(buff);

		CCLOG("url: %s", (strURL+"?"+strContent).c_str());

		HGCurlHelper *helper = HGCurlHelper::initCurlHelper();
		helper->post(strURL,strContent,pSender,callFunc);
	}

	/*
	 * Class:     com_izhangxin_platform_downjoy_AndroidPlatformInterface
	 * Method:    onDJLoginCancel
	 * Signature: ()V
	 */
	JNIEXPORT void JNICALL Java_com_izhangxin_platform_downjoy_AndroidPlatformInterface_onDJLoginCancel
	  (JNIEnv *env, jclass cls)
	{
		CCLOG(__FUNCTION__);

		IPlatformEvent *event = IPlatformEvent::create(kIPlatformEventLoginDidCancel, NULL);
		if(event)
			event->push();
	}

	/*
	 * Class:     com_izhangxin_platform_downjoy_AndroidPlatformInterface
	 * Method:    onDJLoginFail
	 * Signature: (Ljava/lang/String;)V
	 */
	JNIEXPORT void JNICALL Java_com_izhangxin_platform_downjoy_AndroidPlatformInterface_onDJLoginFail
	  (JNIEnv *env, jclass cls, jstring jerror)
	{
		CCLOG(__FUNCTION__);

		string error_msg = JniHelper::jstring2string(jerror);

		IPlatformEvent *event = IPlatformEvent::create(kIPlatformEventLoginFail, &error_msg);
		if(event)
			event->pushInCurThread();
	}

	/*
	 * Class:     com_izhangxin_platform_downjoy_AndroidPlatformInterface
	 * Method:    onDJPurchaseSuccess
	 * Signature: ()V
	 */
	JNIEXPORT void JNICALL Java_com_izhangxin_platform_downjoy_AndroidPlatformInterface_onDJPurchaseSuccess
	  (JNIEnv *, jclass)
	{
		CCLOG(__FUNCTION__);

		IPlatformEvent *event = IPlatformEvent::create(kIPlatformEventPayResultSuccess, NULL);
		if(event)
			event->push();
	}

	/*
	 * Class:     com_izhangxin_platform_downjoy_AndroidPlatformInterface
	 * Method:    onDJPurchaseCancel
	 * Signature: ()V
	 */
	JNIEXPORT void JNICALL Java_com_izhangxin_platform_downjoy_AndroidPlatformInterface_onDJPurchaseCancel
	  (JNIEnv *, jclass)
	{
		CCLOG(__FUNCTION__);

		IPlatformEvent *event = IPlatformEvent::create(kIPlatformEventPayResultCancel, NULL);
		if(event)
			event->push();
	}

	/*
	 * Class:     com_izhangxin_platform_downjoy_AndroidPlatformInterface
	 * Method:    onDJPurchaseFail
	 * Signature: (Ljava/lang/String;)V
	 */
	JNIEXPORT void JNICALL Java_com_izhangxin_platform_downjoy_AndroidPlatformInterface_onDJPurchaseFail
	  (JNIEnv *, jclass, jstring)
	{
		CCLOG(__FUNCTION__);
	}
}
