//
//  IDSPlatformHandler.mm
//  zjh
//
//  Created by admin on 13-1-5.
//
//

#import "stdafx.h"
#import "IDSPlatformHandler.h"
#import <IDS/idsapi.h>
#import "IPlatformEvent.h"
#import "data_module.h"
#import "game_bdohttp_proto.h"

@implementation IDSPlatformHandler

CCNode *pSender = NULL;
SEL_CallFuncN callFunc = NULL;

static IDSPlatformHandler* handler = nil;

+ (IDSPlatformHandler*) sharedInstance
{
    if(handler == nil)
    {
        handler = [[IDSPlatformHandler alloc] init];
        [handler autorelease];
        [handler retain];
    }
    
    return handler;
}

+ (void) destory
{
    if(handler != nil)
        [handler release];
}

- (void)LoginNotificationProcess:(NSNotification *) notif {
    
    NSDictionary *userinfo = [notif userInfo];
    NSLog(@"userinfo: %@", userinfo);
    
    bool isSuccess = [[userinfo objectForKey:@"result"]boolValue];
    if ([[idsPP sharedInstance] isLogined] && isSuccess) {
        // 登录成功后的操作
        
        // 获取用户id
        NSString *userid = [userinfo objectForKey:@"userid"];
        // 获取用户昵称
        NSString *nickname = [userinfo objectForKey:@"nickname"];
        // 获取用户usertoken
        NSString *usertoken = [userinfo objectForKey:@"usertoken"];
        
//        m_lbl.text = [NSString stringWithFormat:@"登录成功 userid:%@,nickname:%@,usertoken:%@",userid,nickname,usertoken];
        
        IPlatformEvent *event = IPlatformEvent::create(kIPlatformEventLogout, NULL);
        if(event)
            event->push();
        
        if(pSender && callFunc)
        {
            IPlatformEvent *event = IPlatformEvent::create(kIPlatformEventLoginWillLoad, NULL);
            if(event)
                event->push();
            
            string strURL = GameBDOHttpProto::shareGameBDOHttpProto()->getURLPre();
            strURL.append(HGResMgr::shareResMgr()->getString("yd_account_login_url"));
            
            char buff[512];
            sprintf(buff,"userid=%s&token=%s&imei=%s&imeiname=%s&pn=%s&nick=%s&desc=%s",[userid UTF8String], [usertoken UTF8String],
                    DataModule::GetInstancePtr()->device_data().phone_imei_.c_str(),
                    DataModule::GetInstancePtr()->device_data().phone_name_.c_str(),
                    //NdComPlatformNickName().c_str(),
                    DataModule::GetInstancePtr()->device_data().game_packet_name_.c_str(),
                    [nickname UTF8String],
                    "");
            
            string strContent(buff);
            
            CCLOG("addr:%s?%s", strURL.c_str(), strContent.c_str());

			HGCurlHelper* helper = HGCurlHelper::initCurlHelper();
			helper->post(strURL, strContent, pSender, callFunc);
        }
        else
        {
            IPLoginParams *param = new IPLoginParams();
            if(param)
            {
                param->autorelease();
                param->retain();
                
                param->param1 = [userid UTF8String];
                param->param2 = [usertoken UTF8String];
                param->param3 = [nickname UTF8String];
                
                IPlatformEventObject *object = IPlatformEventObject::create(kIPlatformEventLoginDidFinish, param);
                if(object)
                    object->push();
            }
        }
        
        CCTouchDispatcher::sharedDispatcher()->setDispatchEvents(true);
    
    }else {
        // 登录失败
        int errcode = [[userinfo objectForKey:@"errcode"] intValue];
        if ( errcode == IDS_ERROR_LOGIN_CANCEL ) {
            //用户取消登录和相应处理
//            m_lbl.text = @"取消登录";
        } else {
            //用户登录失败处理（一般不用处理）
//            m_lbl.text = @"用户登录失败";
        }
    }
}

- (void)LogoutNotificationProcess:(NSNotification *) notif {
    
    NSDictionary *userinfo = [notif userInfo];
    bool isSuccess = [[userinfo objectForKey:@"result"]boolValue];
    int mode = [[userinfo objectForKey:@"mode"] intValue]; // 1:调用用户信息页面的注销按钮后 2:程序调用注销接口
    
    if (isSuccess) {
        // 注销成功后的操作
        // 获取用户id
        NSString *userid = [userinfo objectForKey:@"userid"];
        // 获取用户昵称
        NSString *nickname = [userinfo objectForKey:@"nickname"];
        // 获取用户usertoken
        NSString *usertoken = [userinfo objectForKey:@"usertoken"];
        
//        m_lbl1.text = [NSString stringWithFormat:@"注销成功 %@,%@,%d,usertoken:%@",userid,nickname,mode,usertoken];
        
        //[[idsPP sharedInstance] showLoginWithOrientation:IDSInterfaceOrientationPortraitUp];
    }else {
        
        if (mode == 2) {
            // 注销失败（一般不用处理）
            int errcode = [[userinfo objectForKey:@"errcode"] intValue];
            if (errcode == IDS_ERROR_NETWORK) {
                
//                m_lbl1.text = @"注销失败,网络异常";
                
            }else if (errcode == IDS_ERROR_SERVER) {
                
//                m_lbl1.text = @"注销失败,接口异常";
                
            }
        }
        
    }
    
}

- (void)closeProf:(NSNotification *) notif
{
    CCLOG(__FUNCTION__);
    
    CCTouchDispatcher::sharedDispatcher()->setDispatchEvents(true);
}

@end