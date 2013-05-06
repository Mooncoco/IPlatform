#include "stdafx.h"
#include "data_module.h"
#include "IPlatformInterface.h"
#include "platform/android/jni/JniHelper.h"

#include "game_define.h"
#include "lobby_session.h"
#include "charge_result_layer.h"
#include "scene_controller.h"

class OnPlatformPayResultCallback: public CCObject
{
private:
	jint m_nCode;
public:
	OnPlatformPayResultCallback(jint nCode){m_nCode = nCode;}
public:
	void callback(ccTime time)
	{
		int nResultStatus = ChargeResultLayer::kResultFail;

		if(m_nCode == PAY_SUCCESS)
			nResultStatus = ChargeResultLayer::kResultSuccess;
		else if(m_nCode == PAY_CANCEL)
			nResultStatus = ChargeResultLayer::kResultCancel;

		LobbySession::GetInstancePtr()->SwitchPayResultStatus(nResultStatus);

		CCScheduler::sharedScheduler()->unscheduleSelector(schedule_selector(OnPlatformPayResultCallback::callback), this);
	}
};
string strHeadFacePath = "";

bool IPlatformInit() { return true; }
    
bool IPlatformInitWithParams(const char* app_id, const char* app_key, const char* pay_id) { return true; }
    
bool IPlatformIsLogined() { return false; }

void IPlatformLogin() {}
    
void IPlatformLogout() {}

void IPlatformClear() {}
    
void IPlatformEnterPlatformCenter() {}
    
extern "C"
{
	void IPlatformPayInit()
	{
		JniMethodInfo t;
		if (JniHelper::getStaticMethodInfo(t
			, "com/izhangxin/anzhi/android/handler/IPlatformHandler"
			, "IPlatformPayInit"
			, "()V"))
		{
			t.env->CallStaticVoidMethod(t.classID, t.methodID);
			t.env->DeleteLocalRef(t.classID);
		}else
		{
			CCAssert(false, "com.izhangxin.anzhi.android.handler.IPlatformHandler not valid!");
		}
	}

	void IPlatformPay(const char* order, Chest chest) 
	{
		JniMethodInfo t;
		if (JniHelper::getStaticMethodInfo(t
			, "com/izhangxin/anzhi/android/handler/IPlatformHandler"
			, "IPlatformPay"
			, "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;FFI)V"))
		{
			jstring pOrder = t.env->NewStringUTF(order);
			jstring pProductName = t.env->NewStringUTF(chest.goods_name_.c_str());
			jstring pProductDesc = t.env->NewStringUTF(chest.desc_.c_str());
			jstring pProductSerino = t.env->NewStringUTF(chest.serialno_.c_str());

			float fProductPrice = chest.sale_money_;
			float fProductOrignalPrice  = chest.original_money_;
			int nProductCount = 1;

			t.env->CallStaticIntMethod(t.classID, t.methodID, pOrder, pProductName, pProductDesc, pProductSerino, fProductPrice, fProductOrignalPrice,nProductCount );
			t.env->DeleteLocalRef(t.classID);
			t.env->DeleteLocalRef(pOrder);
			t.env->DeleteLocalRef(pProductName);
			t.env->DeleteLocalRef(pProductDesc);
			t.env->DeleteLocalRef(pProductSerino);
		}else
		{
			CCAssert(false, "com.izhangxin.anzhi.android.handler.IPlatformHandler not valid!");
		}
	}

	void IPlatformInitHeadFace() 
	{
		JniMethodInfo t;
		if (JniHelper::getStaticMethodInfo(t
			, "com/izhangxin/platform/AndroidPlatformInterface"
			, "initPhotoWithPath"
			, "(Ljava/lang/String;)V"))
		{
			jstring jpath = t.env->NewStringUTF("/sdcard/com.bdo/face");

			t.env->CallStaticVoidMethod(t.classID, t.methodID, jpath);
			t.env->DeleteLocalRef(t.classID);
			t.env->DeleteLocalRef(jpath);

		}else
		{
			CCAssert(false, "com.izhangxin.platform.AndroidPlatformInterface not valid!");
		}
	}

	void Java_com_izhangxin_anzhi_android_handler_IPlatformHandler_onPayResult(JNIEnv*  env, jobject obj, jint code)
	{
		CCLOG("Java_com_izhangxin_dx_android_handler_IPlatformHandler_onPayResult %ld", code);
		OnPlatformPayResultCallback* callback = new OnPlatformPayResultCallback(code);
		callback->autorelease();
		CCScheduler::sharedScheduler()->scheduleSelector(schedule_selector(OnPlatformPayResultCallback::callback), callback, 0, false);
	}

}

CCTexture2D* IPlatformGetHeadFace() { return NULL; }
    
void IPlatformShareWithScreenShot(const char* pText) {}
    
void IPlatformShareWithImageUrl(const char* text, const char* path) {}

void IPlatformUploadHeadFace(const char* uploadUrl, const char* filePath, const char* guid) {}

void NdSwitchAccount(){};

bool SDCardIsAvailable()
{
	return true;
}
