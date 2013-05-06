#include "stdafx.h"
#include "platform/android/jni/JniHelper.h"
#include "IPlatform.h"
#include "game_bdohttp_proto.h"
#include "data_module.h"
#include "game_define.h"

std::string strHeadFacePath = "";
CCNode *pSender = NULL;
SEL_CallFuncN callFunc = NULL;

bool IPlatformInit()
{
	bool result = false;

	JniMethodInfo t;
	if (JniHelper::getStaticMethodInfo(t
		, "com/izhangxin/platform/miui/AndroidPlatformInterface"
		, "MIUIInit"
		, "()Z"))
	{
		result = t.env->CallStaticBooleanMethod(t.classID, t.methodID);
		t.env->DeleteLocalRef(t.classID);

		return result;

	}else
	{
		CCAssert(false, "com.izhangxin.platform.miui.AndroidPlatformInterface not valid!");
	}

	return result;
}

bool IPlatformInitWithParams(const char* app_id, const char* app_key, const char* pay_id) { return true; }

bool IPlatformIsLogined() { return false; }

void IPlatformLogin()
{
	JniMethodInfo t;
	if (JniHelper::getStaticMethodInfo(t
		, "com/izhangxin/platform/miui/AndroidPlatformInterface"
		, "MIUILogin"
		, "()V"))
	{
		t.env->CallStaticVoidMethod(t.classID, t.methodID);
		t.env->DeleteLocalRef(t.classID);

	}else
	{
		CCAssert(false, "com.izhangxin.platform.miui.AndroidPlatformInterface not valid!");
	}
}

void IPlatformLoginWithCallBack(CCNode* node, SEL_CallFuncN call)
{
	pSender = node;
	callFunc = call;

	IPlatformLogin();
}

void IPlatformLogout() {}

void IPlatformClear() {}

void IPlatformEnterPlatformCenter() {}

void IPlatformPay(const char* order, Chest chest)
{
	CCLOG(__FUNCTION__);

	JniMethodInfo t;
	if (JniHelper::getStaticMethodInfo(t
		, "com/izhangxin/platform/miui/AndroidPlatformInterface"
		, "MIUIPay"
		, "(Ljava/lang/String;Ljava/lang/String;)V"))
	{
		jstring jstrOrder, jproductID;
		jstrOrder = t.env->NewStringUTF(order);
		jproductID = t.env->NewStringUTF(chest.serialno_.c_str());

		t.env->CallStaticVoidMethod(t.classID, t.methodID, jstrOrder, jproductID);
		t.env->DeleteLocalRef(t.classID);
		t.env->DeleteLocalRef(jstrOrder);
		t.env->DeleteLocalRef(jproductID);

	}else
	{
		CCAssert(false, "com.izhangxin.platform.miui.AndroidPlatformInterface not valid!");
	}

}

void IPlatformInitHeadFace()
{
}

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
	 * Class:     com_izhangxin_platform_miui_AndroidPlatformInterface
	 * Method:    MIUILoginCallBack
	 * Signature: (Ljava/lang/String;Ljava/lang/String;)V
	 */
	JNIEXPORT void JNICALL Java_com_izhangxin_platform_miui_AndroidPlatformInterface_MIUILoginCallBack
	  (JNIEnv *env, jclass jcls, jstring juid, jstring jsession, jstring jnickname)
	{
		CCLOG(__FUNCTION__);

		if(pSender && callFunc)
		{
			IPlatformEvent *object = IPlatformEvent::create(kIPlatformEventLoginWillLoad, NULL);
			if(object)
				object->push();

			string strURL = GameBDOHttpProto::shareGameBDOHttpProto()->getUrlPre();
			strURL.append("xm/authen");

			string strNickname = JniHelper::jstring2string(jnickname);
			if(strNickname.empty())
				strNickname = DataModule::GetInstancePtr()->device_data().phone_name_;

			char buff[256];
			sprintf(buff, "uid=%s&session=%s&pn=%s&imei=%s&imeiname=%s",
					JniHelper::jstring2string(juid).c_str(),
					JniHelper::jstring2string(jsession).c_str(),
					DataModule::GetInstancePtr()->device_data().game_packet_name_.c_str(),
					DataModule::GetInstancePtr()->device_data().phone_imei_.c_str(),
					strNickname.c_str());

			string strContent(buff);

			CCLOG("url: %s", (strURL+"?"+strContent).c_str());

			HGCurlHelper *helper = HGCurlHelper::initCurlHelper();
			helper->post(strURL,strContent,pSender,callFunc);
		}
		else
		{
			CCAssert(false, "Login Fail because the pSender or callFunc is null");
		}
	}

	/*
	 * Class:     com_izhangxin_platform_miui_AndroidPlatformInterface
	 * Method:    MIUILoginFailCallBack
	 * Signature: (I)V
	 */
	JNIEXPORT void JNICALL Java_com_izhangxin_platform_miui_AndroidPlatformInterface_MIUILoginFailCallBack
	  (JNIEnv *env, jclass jcls, jint jerror_code)
	{
		CCLOG(__FUNCTION__);

		int error_code = jerror_code;

		IPlatformEvent *object = IPlatformEvent::create(kIPlatformEventLoginFail, (void*)error_code);
		if(object)
			object->push();
	}

	/*
	 * Class:     com_izhangxin_platform_miui_AndroidPlatformInterface
	 * Method:    MIUILoginCancelCallBack
	 * Signature: ()V
	 */
	JNIEXPORT void JNICALL Java_com_izhangxin_platform_miui_AndroidPlatformInterface_MIUILoginCancelCallBack
	  (JNIEnv *env, jclass jcls)
	{
		CCLOG(__FUNCTION__);

		IPlatformEvent *object = IPlatformEvent::create(kIPlatformEventLoginDidCancel, NULL);
		if(object)
			object->push();
	}

	/*
	 * Class:     com_izhangxin_platform_miui_AndroidPlatformInterface
	 * Method:    MIUIPaySuccessCallBack
	 * Signature: ()V
	 */
	JNIEXPORT void JNICALL Java_com_izhangxin_platform_miui_AndroidPlatformInterface_MIUIPaySuccessCallBack
	  (JNIEnv *, jclass)
	{
		CCLOG(__FUNCTION__);

		IPlatformEvent *object = IPlatformEvent::create(kIPlatformEventPayResultSuccess, NULL);
		if(object)
			object->push();
	}

	/*
	 * Class:     com_izhangxin_platform_miui_AndroidPlatformInterface
	 * Method:    MIUIPayCancelCallBack
	 * Signature: ()V
	 */
	JNIEXPORT void JNICALL Java_com_izhangxin_platform_miui_AndroidPlatformInterface_MIUIPayCancelCallBack
	  (JNIEnv *, jclass)
	{
		CCLOG(__FUNCTION__);

		IPlatformEvent *object = IPlatformEvent::create(kIPlatformEventPayResultCancel, NULL);
		if(object)
			object->push();
	}

	/*
	 * Class:     com_izhangxin_platform_miui_AndroidPlatformInterface
	 * Method:    MIUIPayFailCallBack
	 * Signature: ()V
	 */
	JNIEXPORT void JNICALL Java_com_izhangxin_platform_miui_AndroidPlatformInterface_MIUIPayFailCallBack
	  (JNIEnv *, jclass)
	{
		CCLOG(__FUNCTION__);

		IPlatformEvent *object = IPlatformEvent::create(kIPlatformEventPayResultFail, NULL);
		if(object)
			object->push();
	}
}

