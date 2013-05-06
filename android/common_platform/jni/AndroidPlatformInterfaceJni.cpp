#include "stdafx.h"
#include "platform/android/jni/JniHelper.h"
#include "IPlatform.h"
#include "data_module.h"
#include "game_define.h"

std::string strHeadFacePath = "";

bool IPlatformInit() { return true; }

bool IPlatformInitWithParams(const char* app_id, const char* app_key, const char* pay_id) { return true; }

bool IPlatformIsLogined() { return false; }

void IPlatformLogin() {}

void IPlatformLogout() {}

void IPlatformClear() {}

void IPlatformEnterPlatformCenter() {}

void IPlatformPay(const char* order, Chest chest) {}

void IPlatformInitHeadFace() 
{
	JniMethodInfo t;
	if (JniHelper::getStaticMethodInfo(t
		, "com/izhangxin/platform/AndroidPlatformInterface"
		, "initHeadFaceWithPath"
		, "(Ljava/lang/String;)V"))
	{
		jstring jpath = t.env->NewStringUTF("/sdcard/com.bdo/");

		t.env->CallStaticVoidMethod(t.classID, t.methodID, jpath);
		t.env->DeleteLocalRef(t.classID);
		t.env->DeleteLocalRef(jpath);

	}else
	{
		CCAssert(false, "com.izhangxin.platform.AndroidPlatformInterface not valid!");
	}
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

extern "C"
{

	JNIEXPORT void JNICALL Java_com_izhangxin_platform_AndroidPlatformInterface_initHeadFaceDidFinish
	  (JNIEnv *env, jclass cls, jstring str)
	{
		CCLog("Java_com_izhangxin_platform_AndroidPlatformInterface_initHeadFaceDidFinish");

		strHeadFacePath = JniHelper::jstring2string(str);

		if(strHeadFacePath.empty())
			return;

		CCTexture2D *texture = CCTextureCache::sharedTextureCache()->addImage(strHeadFacePath.c_str());

		IPlatformEvent *object = IPlatformEvent::create(UPDATE_EVENT_PLATFORM_HEAD_FACE, (void*)texture);
		if(object)
			object->push();
	}

}

