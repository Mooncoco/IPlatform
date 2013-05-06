
#include "stdafx.h"
#include "platform/android/jni/JniHelper.h"

#include "scene_controller.h"
#include "loading_layer.h"
#include "game_define.h"

class OnPushEventToLoadingLayerCallback: public CCObject
{
private:
	int m_nEvent;
    
public:
	OnPushEventToLoadingLayerCallback(int nEvent)
	{             
		m_nEvent = nEvent;
	}

	void callback(ccTime time)
	{
		LoadingLayer* pLoadingLayer = dynamic_cast<LoadingLayer*>(SceneController::GetInstancePtr()->getCurLayer());
        if(pLoadingLayer != NULL)
        {
            pLoadingLayer->PushEvent(m_nEvent,NULL);
        }
    	CCScheduler::sharedScheduler()->unscheduleSelector(schedule_selector(OnPushEventToLoadingLayerCallback::callback), this);
	}
};
class OnGetSinaLoginSuccessCallback: public CCObject
{
private:
	string m_strUin ; 
    string m_strSessionId ;
public:
	OnGetSinaLoginSuccessCallback(const string& strUin, const string& strSessionId)
	{
		m_strUin = strUin;
		m_strSessionId = strSessionId;
	}
public:
	void callback(ccTime time)
	{
		LoadingLayer* pLoadingLayer = dynamic_cast<LoadingLayer*>(SceneController::GetInstancePtr()->getCurLayer());
    	if(pLoadingLayer != NULL)
    	{
    		pLoadingLayer->sinaLogin(m_strUin, m_strSessionId);
    	}
    	CCScheduler::sharedScheduler()->unscheduleSelector(schedule_selector(OnGetSinaLoginSuccessCallback::callback), this);
	}
};

extern "C"
{
	/*
	void SinaLogin()
	{
        [[SinaHandler shareSinaHandler]SinaLogin];
	};
	*/
	void SinaLogin()
    {
    	JniMethodInfo t;
		if (JniHelper::getStaticMethodInfo(t
			, "com/izhangxin/platform/SinaHandler"
			, "SinaPlatformLogin"
			, "()V"))
		{
			t.env->CallStaticVoidMethod(t.classID, t.methodID);
			t.env->DeleteLocalRef(t.classID);
		}else
		{
			CCAssert(false, "com.izhangxin.platform.SinaHandler not valid!");
		}
    }
	
    /*
    void SinaLogout()
	{
        [[SinaHandler shareSinaHandler]SinaLogout];
	};
    */
	void SinaLogout()
    {
    	JniMethodInfo t;
		if (JniHelper::getStaticMethodInfo(t
			, "com/izhangxin/platform/SinaHandler"
			, "SinaPlatformLogout"
			, "()V"))
		{
			t.env->CallStaticVoidMethod(t.classID, t.methodID);
			t.env->DeleteLocalRef(t.classID);
		}else
		{
			CCAssert(false, "com.izhangxin.platform.SinaHandler not valid!");
		}
    }
	
	/*
    void SinaInit()
    {
        [[SinaHandler shareSinaHandler]SinaInit]; 
	};
    */
	void SinaInit()
    {
    	JniMethodInfo t;
		if (JniHelper::getStaticMethodInfo(t
			, "com/izhangxin/platform/SinaHandler"
			, "SinaPlatformInit"
			, "()V"))
		{
			t.env->CallStaticVoidMethod(t.classID, t.methodID);
			t.env->DeleteLocalRef(t.classID);
		}else
		{
			CCAssert(false, "com.izhangxin.platform.SinaHandler not valid!");
		}
    }
	
	/*
	int SinaShareToThirdPlatformWithScreenShot(const string& strText)
    {
        return [[SinaHandler shareSinaHandler] SinaShareToThirdPlatformWithScreenShot:[NSString stringWithUTF8String:strText.c_str()]];
	};
	*/
	int SinaShareToThirdPlatformWithScreenShot(const string& strText)
    {
        JniMethodInfo t;
		if (JniHelper::getStaticMethodInfo(t
			, "com/izhangxin/platform/SinaHandler"
			, "SinaShareToThirdPlatformWithRGB"
			, "(Ljava/lang/String;[III)I"))
		{
			int x = CCEGLView::sharedOpenGLView().getViewPort().origin.x;
			int y = CCEGLView::sharedOpenGLView().getViewPort().origin.y;
			int w = CCEGLView::sharedOpenGLView().getViewPort().size.width;
			int h = CCEGLView::sharedOpenGLView().getViewPort().size.height;
			
			jint *buff = (jint*)malloc(w * h * sizeof(jint));
			glReadPixels(x, y, w, h, GL_RGBA, GL_UNSIGNED_BYTE, buff);
			
			jint *fliped = (jint*)malloc(w * h * sizeof(jint));
			for(int i = 0; i < h; i++)
			{ 
				for(int j = 0; j < w; j++)
				{
					int pix = buff[i*w+j];
					int pb = (pix>>16)&0xffL;
					int pr = (pix<<16)&0x00ff0000L;
					int pix1 = (pix&0xff00ff00L) | pr | pb;
					fliped[(h-i-1)*w+j] = pix1;
				}
			}   
			free(buff);;
			jintArray intArr = t.env->NewIntArray(w * h);
			t.env->SetIntArrayRegion(intArr, 0, w * h, fliped);
			free(fliped);;
			
			jstring jstrText = t.env->NewStringUTF(strText.c_str());
			int result = t.env->CallStaticIntMethod(t.classID, t.methodID, jstrText, intArr, w, h);
			t.env->DeleteLocalRef(t.classID);
			t.env->DeleteLocalRef(jstrText);
			t.env->DeleteLocalRef(intArr);
		}else
		{
			CCAssert(false, "com.izhangxin.platform.NdComPlatformHandler not valid!");
		}
    }
    
    int SinaShareToThirdPlatformWithFile(const string& strText, const string& strFile)
    {
    	JniMethodInfo t;
    	int result = -1;
		if (JniHelper::getStaticMethodInfo(t
			, "com/izhangxin/platform/SinaHandler"
			, "SinaShareToThirdPlatformWithFile"
			, "(Ljava/lang/String;Ljava/lang/String;)I"))
		{
			jstring jstrText = t.env->NewStringUTF(strText.c_str());
			jstring jstrFile = t.env->NewStringUTF(CCFileUtils::fullPathFromRelativePath(strFile.c_str()));

			result = t.env->CallStaticIntMethod(t.classID, t.methodID, jstrText, jstrFile);
			t.env->DeleteLocalRef(t.classID);
			t.env->DeleteLocalRef(jstrText);
			t.env->DeleteLocalRef(jstrFile);

			return result;
		}else
		{
			CCAssert(false, "com.izhangxin.platform.SinaHandler not valid!");
		}
		
		return result;
    }
	
	void Java_com_izhangxin_platform_SinaHandler_onSinaLoginFail(JNIEnv*  env, jobject obj)
    {
    	OnPushEventToLoadingLayerCallback* callback = new OnPushEventToLoadingLayerCallback(UPDATE_EVENT_ND_LOGIN_FAIL);
    	callback->autorelease();
    	CCScheduler::sharedScheduler()->scheduleSelector(schedule_selector(OnPushEventToLoadingLayerCallback::callback), callback, 0, false);
    }
	void Java_com_izhangxin_platform_SinaHandler_onSinaLoginCancel(JNIEnv*  env, jobject obj)
    {
    	OnPushEventToLoadingLayerCallback* callback = new OnPushEventToLoadingLayerCallback(UPDATE_EVENT_ND_LOGIN_FAIL);
    	callback->autorelease();
    	CCScheduler::sharedScheduler()->scheduleSelector(schedule_selector(OnPushEventToLoadingLayerCallback::callback), callback, 0, false);
    }
	void Java_com_izhangxin_platform_SinaHandler_onSinaLoginSuccess(JNIEnv*  env, jobject obj,jstring uin, jstring sid)
    {
		string strUin = JniHelper::jstring2string(uin); 
    	string strSessionId = JniHelper::jstring2string(sid); 
		CCLOG("Java_com_izhangxin_platform_SinaHandler_onSinaLoginSuccess:%s,%s", strUin.c_str(), strSessionId.c_str());
    	OnGetSinaLoginSuccessCallback* callback = new OnGetSinaLoginSuccessCallback(strUin,strSessionId);
    	callback->autorelease();
    	CCScheduler::sharedScheduler()->scheduleSelector(schedule_selector(OnGetSinaLoginSuccessCallback::callback), callback, 0, false);
    }
}
