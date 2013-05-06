
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
class OnGetTencentLoginSuccessCallback: public CCObject
{
private:
	string m_strUin ; 
    string m_strSessionId ;
public:
	OnGetTencentLoginSuccessCallback(const string& strUin, const string& strSessionId)
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
    		pLoadingLayer->tencentLogin(m_strUin, m_strSessionId);
    	}
    	CCScheduler::sharedScheduler()->unscheduleSelector(schedule_selector(OnGetTencentLoginSuccessCallback::callback), this);
	}
};

extern "C"
{
	/*
	void TencentLogin()
	{
        [[TencentHandler shareTencentHandler]TencentLogin];
	};
	*/
	void TencentLogin()
    {
    	JniMethodInfo t;
		if (JniHelper::getStaticMethodInfo(t
			, "com/izhangxin/platform/tencent/TencentHandler"
			, "TencentPlatformLogin"
			, "()V"))
		{
			t.env->CallStaticVoidMethod(t.classID, t.methodID);
			t.env->DeleteLocalRef(t.classID);
		}else
		{
			CCAssert(false, "com.izhangxin.platform.tencent.TencentHandler not valid!");
		}
    }
	
    /*
    void TencentLogout()
	{
        [[TencentHandler shareTencentHandler]TencentLogout];
	};
    */
	void TencentLogout()
    {
    	JniMethodInfo t;
		if (JniHelper::getStaticMethodInfo(t
			, "com/izhangxin/platform/tencent/TencentHandler"
			, "TencentPlatformLogout"
			, "()V"))
		{
			t.env->CallStaticVoidMethod(t.classID, t.methodID);
			t.env->DeleteLocalRef(t.classID);
		}else
		{
			CCAssert(false, "com.izhangxin.platform.tencent.TencentHandler not valid!");
		}
    }
	
	/*
    void TencentInit()
    {
        [[TencentHandler shareTencentHandler]TencentInit]; 
	};
    */
	void TencentInit()
    {
    	JniMethodInfo t;
		if (JniHelper::getStaticMethodInfo(t
			, "com/izhangxin/platform/tencent/TencentHandler"
			, "TencentPlatformInit"
			, "()V"))
		{
			t.env->CallStaticVoidMethod(t.classID, t.methodID);
			t.env->DeleteLocalRef(t.classID);
		}else
		{
			CCAssert(false, "com.izhangxin.platform.tencent.TencentHandler not valid!");
		}
    }
	
	/*
	int TencentShareToThirdPlatformWithScreenShot(const string& strText)
    {
        return [[TencentHandler shareTencentHandler] TencentShareToThirdPlatformWithScreenShot:[NSString stringWithUTF8String:strText.c_str()]];
	};
	*/
	int TencentShareToThirdPlatformWithScreenShot(const string& strText)
    {
        JniMethodInfo t;
		if (JniHelper::getStaticMethodInfo(t
			, "com/izhangxin/platform/tencent/TencentHandler"
			, "TencentShareToThirdPlatformWithRGB"
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
			CCAssert(false, "com.izhangxin.platform.tencent.TencentHandler not valid!");
		}
    }
    
	/*
	int TencentShareToThirdPlatformWithFile(const string& strText, const string& strFile)
    {
        NSString* pText = [NSString stringWithUTF8String:strText.c_str()];
        NSString* pFile = [NSString stringWithUTF8String:strFile.c_str()];
        
        return [[TencentHandler shareTencentHandler] TencentShareToThirdPlatformWithFile:pText pFile:pFile];
	};
	*/
    int TencentShareToThirdPlatformWithFile(const string& strText, const string& strFile)
    {
    	unsigned long nSize = 0;
    	unsigned char* buff = CCFileUtils::getFileData(strFile.c_str(), "r", &nSize);
    	
    	if(nSize <= 0)
    	{
    		return -1;
    	}
    	
    	JniMethodInfo t;
    	int result = -1;
		if (JniHelper::getStaticMethodInfo(t
			, "com/izhangxin/platform/tencent/TencentHandler"
			, "TencentShareToThirdPlatformWithImageBuffer"
			, "(Ljava/lang/String;[B)I"))
		{
			jbyteArray jbuff = t.env->NewByteArray(nSize);
			t.env->SetByteArrayRegion(jbuff, 0, nSize, (jbyte*)buff);
			
			jstring jstrText = t.env->NewStringUTF(strText.c_str());
			 result = t.env->CallStaticIntMethod(t.classID, t.methodID, jstrText, jbuff);
			t.env->DeleteLocalRef(t.classID);
			t.env->DeleteLocalRef(jstrText);
			t.env->DeleteLocalRef(jbuff);
			return result;
		}else
		{
			CCAssert(false, "com.izhangxin.platform.tencent.TencentHandler not valid!");
		}
		
		delete[] buff;
		return result;
    }
	
	int TencentShareToThirdPlatformWithURL(const string& strText, const string& strURL)
    {
		if(!HGDownloader::isValidURL(strURL))
		{
			CCMessageBox(string(strURL).append(" is not a valid URL !").c_str(), "Warning");
			return -1;
		}

    	JniMethodInfo t;
    	int result = -1;
		if (JniHelper::getStaticMethodInfo(t
			, "com/izhangxin/platform/tencent/TencentHandler"
			, "TencentShareToThirdPlatformWithURL"
			, "(Ljava/lang/String;Ljava/lang/String;)I"))
		{
			jstring jstrText = t.env->NewStringUTF(strText.c_str());
			jstring jstrURL = t.env->NewStringUTF(strURL.c_str());

			 result = t.env->CallStaticIntMethod(t.classID, t.methodID, jstrText, jstrURL);
			t.env->DeleteLocalRef(t.classID);
			t.env->DeleteLocalRef(jstrText);
			t.env->DeleteLocalRef(jstrURL);
			return result;
		}else
		{
			CCAssert(false, "com.izhangxin.platform.tencent.TencentHandler not valid!");
		}

        return 1;
	};
	
	void Java_com_izhangxin_platform_tencent_TencentHandler_onTencentLoginFail(JNIEnv*  env, jobject obj)
    {
    	OnPushEventToLoadingLayerCallback* callback = new OnPushEventToLoadingLayerCallback(UPDATE_EVENT_ND_LOGIN_FAIL);
    	callback->autorelease();
    	CCScheduler::sharedScheduler()->scheduleSelector(schedule_selector(OnPushEventToLoadingLayerCallback::callback), callback, 0, false);
    }
	void Java_com_izhangxin_platform_tencent_TencentHandler_onTencentLoginCancel(JNIEnv*  env, jobject obj)
    {
    	OnPushEventToLoadingLayerCallback* callback = new OnPushEventToLoadingLayerCallback(UPDATE_EVENT_ND_LOGIN_FAIL);
    	callback->autorelease();
    	CCScheduler::sharedScheduler()->scheduleSelector(schedule_selector(OnPushEventToLoadingLayerCallback::callback), callback, 0, false);
    }
	void Java_com_izhangxin_platform_tencent_TencentHandler_onTencentLoginSuccess(JNIEnv*  env, jobject obj,jstring uin, jstring sid)
    {
		string strUin = JniHelper::jstring2string(uin); 
    	string strSessionId = JniHelper::jstring2string(sid);
    	OnGetTencentLoginSuccessCallback* callback = new OnGetTencentLoginSuccessCallback(strUin,strSessionId);
    	callback->autorelease();
    	CCScheduler::sharedScheduler()->scheduleSelector(schedule_selector(OnGetTencentLoginSuccessCallback::callback), callback, 0, false);
    }
}
