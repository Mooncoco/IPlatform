//
//  IPlatformInterface.h
//
//  Created by zengjunchao on 12-11-26.
//
//

#ifndef _IPlatformInterface_h
#define _IPlatformInterface_h

#if (GAME_PLATFORM_TARGET == GAME_PLATFORM_ANDROID_LENOVO)

#include "DDPlatformInterfaceExtension.h"
#include "LenovoPlatformInterfaceExtension.h"
#endif

class Chest;

extern "C"
{
    extern bool IPlatformInit();
    
    extern bool IPlatformInitWithParams(const char* app_id, const char* app_key, const char* pay_id);
    
    extern void IPlatformLogin();
    
    extern void IPlatformLoginWithCallBack(CCNode* node, SEL_CallFuncN callFunc);
    
    extern void IPlatformLogout();
    
    extern void IPlatformClear();
    
    extern bool IPlatformIsLogined();
    
    extern void IPlatformEnterPlatformCenter();

    extern void IPlatformPayInit();

    extern void IPlatformPay(const char* order, Chest chest);

	extern void IPlatformPayQuick(const char* order, Chest chest,int gameId);

    extern void IPlatformPurchase(const char* order, const char* productName, int price, const char* param, ...);

    extern void IPlatformPurchaseWithType(int nType, const char* order, const char* productName, int price, const char* param, ...);
    
    extern CCTexture2D* IPlatformGetHeadFace();
    
    extern void IPlatformInitHeadFace();
    
    extern void IPlatformUploadHeadFace(const char* updateUrl, const char* filePath, const char* guid);
    
    extern void IPlatformShareWithScreenShot(const char* pText);
    
    extern void IPlatformShareWithImageUrl(const char* text, const char* path);
    
	extern void IPlatformSwitchAccount();
	
	extern bool IPlatformExternalStorageState();


}


#endif
