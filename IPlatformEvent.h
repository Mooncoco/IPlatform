//
//  IPlatformEvent.h
//  IPlatform Interface
//
//  Created by zengjunchao on 12-11-13.
//
//

#ifndef __IPlatform_event__
#define __IPlatform_event__

#include "scene_controller.h"

enum IPlatformEventEnum
{
    kIPlatformEventLoginWillLoad = 30000,
    kIPlatformEventLoginDidFinish,
    kIPlatformEventLoginDidCancel,
    kIPlatformEventLoginFail,
    
    kIPlatformEventLogout,
    
    kIPlatformEventPhotoNative,
    kIPlatformEventPhotoUploadFinish,
    kIPlatformEventPhotoUploadFail,
    
    kIPlatformEventPayResultSuccess,
	kIPlatformEventPaySmSSuccess,
	kIPlatformEventPaySmSClient,
	kIPlatformEventPayResultCancel,
	kIPlatformEventPayResultFail
};

extern string strHeadFacePath;

class IPlatformEvent : public CCObject
{
public:
    
	IPlatformEvent(void) : nEventID(-1), param(NULL) {}
    
	static IPlatformEvent* create(int nEventID, void* param)
	{
		IPlatformEvent* object = new IPlatformEvent();
        if(object && object->init(nEventID, param))
        {
            return object;
        }
        CC_SAFE_DELETE(object)
		return NULL;
	}
    
	virtual bool init(int nEventID, void* param)
	{
		this->nEventID = nEventID;
		this->param = param;
        
		return true;
	}
    
	void update(ccTime dt)
	{
		execute();
        CCScheduler::sharedScheduler()->unscheduleUpdateForTarget(this);
	}
	
	void push()
	{
		CCScheduler::sharedScheduler()->scheduleUpdateForTarget(this, 0, false);
	}
    
    void pushInCurThread()
    {
        execute();
    }
    
protected:
    
    virtual void execute()
    {
        SceneController::GetInstancePtr()->PushEventToCurrent(nEventID, param);
    }
    
	int nEventID;
	void* param;
};

class IPLoginParams : public CCObject
{
public:
    int nPlatformID;
    string param1;
    string param2;
    string param3;
};

class IPError : public CCObject
{
public:
    int nPlatformID;
    int status;
    string error;
};

class IPlatformEventObject : public IPlatformEvent
{
public:
    
	IPlatformEventObject(void) : objectParam(NULL) {}
    
	static IPlatformEventObject* create(int nEventID, CCObject* param)
	{
		IPlatformEventObject* object = new IPlatformEventObject();
        if(object && object->init(nEventID, param))
        {
            return object;
        }
        CC_SAFE_DELETE(object)
		return NULL;
	}
    
	bool init(int nEventID, void* param)
	{
		this->nEventID = nEventID;
		this->objectParam = (CCObject*)param;
		
		objectParam->retain();
        
		return true;
	}
    
protected:
    
    void execute()
    {
        SceneController::GetInstancePtr()->PushEventToCurrent(nEventID, objectParam);
		CC_SAFE_RELEASE(objectParam)
    }

	CCObject* objectParam;
};

class IPlatformDownloadEventObject : public IPlatformEventObject
{
public:
    
	static IPlatformDownloadEventObject* create(int nEventID, CCObject* param)
	{
		IPlatformDownloadEventObject* object = new IPlatformDownloadEventObject();
        if(object && object->init(nEventID, param))
        {
            return object;
        }
        CC_SAFE_DELETE(object)
		return NULL;
	}

    void onDownloadFinished(CCObject* pSender)
    {
        SceneController::GetInstancePtr()->PushEventToCurrent(nEventID, pSender);
    }
};

#endif /* defined(__IPlatform_event__) */