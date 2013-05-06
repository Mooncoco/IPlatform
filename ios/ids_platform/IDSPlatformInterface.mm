//
//  IDSPlatformInterface.cpp
//  zjh
//
//  Created by admin on 13-1-5.
//
//

#import "stdafx.h"
#import "IPlatformInterface.h"
#import "data_module.h"

#import <IDS/idsapi.h>
#import "IDSPlatformHandler.h"

#import <string>
using std::string;

string strHeadFacePath = "";

bool IPlatformInit()
{
    //init IDS sdk

    [[idsPP sharedInstance] SetAppID:atoi([NSLocalizedStringFromTable(@"AppID", @"IPlatformConfig", nil) UTF8String])
                              AppKey:NSLocalizedStringFromTable(@"AppKey", @"IPlatformConfig", nil)
                            UmengKey:NSLocalizedStringFromTable(@"UmengKey", @"IPlatformConfig", nil)];
    
    [[NSNotificationCenter defaultCenter] addObserver:[IDSPlatformHandler sharedInstance]
                                             selector:@selector(LoginNotificationProcess:)
                                                 name:@"kIDSLoginNotification"
                                               object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:[IDSPlatformHandler sharedInstance]
                                             selector:@selector(LogoutNotificationProcess:)
                                                 name:@"kIDSLogoutNotification"
                                               object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:[IDSPlatformHandler sharedInstance]
                                             selector:@selector(closeProf:)
                                                 name:@"kIDSCloseProfileViewNotification"
                                               object:nil];
    
    return true;
}

bool IPlatformInitWithParams(const char* app_id, const char* app_key, const char* pay_id) { return true; }

bool IPlatformIsLogined() { return [[idsPP sharedInstance] isLogined]; }

void IPlatformLogin()
{
    CCTouchDispatcher::sharedDispatcher()->setDispatchEvents(false);
    
    //IDSInterfaceOrientationPortrait = 5, // 支持竖屏自动转向
    //IDSInterfaceOrientationLandscape = 6 // 支持横屏自动转向
    //所有模块的方向控制
    
    IDSInterfaceOrientation orientation = IDSInterfaceOrientationLandscape;
    
    [[idsPP sharedInstance] showLoginWithOrientation:orientation];
}

void IPlatformLoginWithCallBack(CCNode* node, SEL_CallFuncN func)
{
    pSender = node;
    callFunc = func;
    
    IPlatformLogin();
}

void IPlatformLogout()
{
    // 调用注销接口
    [[idsPP sharedInstance] logout];
}

void IPlatformClear() {}

void IPlatformEnterPlatformCenter()
{
    CCTouchDispatcher::sharedDispatcher()->setDispatchEvents(false);
    
    IDSInterfaceOrientation orientation = IDSInterfaceOrientationLandscape;
    
    // 调用个人信息模块（含 注销、修改密码、修改昵称等功能）
    [[idsPP sharedInstance] showUserProfileWithOrientation:orientation];
}

void IPlatformPayInit(){}

void IPlatformPay(const char* order, Chest chest) {}

void IPlatformInitHeadFace() {}

CCTexture2D* IPlatformGetHeadFace() { return NULL; }

void IPlatformShareWithScreenShot(const char* pText) {}

void IPlatformShareWithImageUrl(const char* text, const char* path) {}

void IPlatformUploadHeadFace(const char* uploadUrl, const char* filePath, const char* guid) {}

void IPlatformSwitchAccount() { IPlatformLogout(); }

bool IPlatformExternalStorageState() {return true;}