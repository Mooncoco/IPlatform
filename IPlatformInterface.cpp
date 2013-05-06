#include "stdafx.h"
#include "IPlatformInterface.h"
#include "data_module.h"

string strHeadFacePath = "";

bool IPlatformInit() { return true; }
    
bool IPlatformInitWithParams(const char* app_id, const char* app_key, const char* pay_id) { return true; }
    
bool IPlatformIsLogined() { return false; }

void IPlatformLogin() {}

void IPlatformLoginWithCallBack(CCNode* node, SEL_CallFuncN callFunc) {}
    
void IPlatformLogout() {}

void IPlatformClear() {}
    
void IPlatformEnterPlatformCenter() {}
    
void IPlatformPayInit(){}

void IPlatformPay(const char* order, Chest chest) {}

void IPlatformPayQuick(const char* order, Chest chest,int gameId) {}
    
void IPlatformInitHeadFace() {}

CCTexture2D* IPlatformGetHeadFace() { return NULL; }
    
void IPlatformShareWithScreenShot(const char* pText) {}
    
void IPlatformShareWithImageUrl(const char* text, const char* path) {}

void IPlatformUploadHeadFace(const char* uploadUrl, const char* filePath, const char* guid) {}

void IPlatformSwitchAccount() {}

bool IPlatformExternalStorageState() {return true;}

#if (CC_TARGET_PLATFORM == CC_PLATFORM_WIN32)
	void DDPlatformPurchase(const char* order, Chest chest, const char* param, ...){}
	void LenovoPlatformPurchase(const char* order, Chest chest, const char* param, ...){}
#endif
