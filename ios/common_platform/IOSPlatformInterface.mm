#import "stdafx.h"
#import "IPlatformInterface.h"
#import "data_module.h"

#import "ImagePickerController.h"

//default

string strHeadFacePath = "";

bool IPlatformInit() { strHeadFacePath = ""; return true; }
    
bool IPlatformInitWithParams(const char* app_id, const char* app_key, const char* pay_id) { return IPlatformInit(); }
    
bool IPlatformIsLogined() { return false; }

void IPlatformLogin() {}
    
void IPlatformLogout() { strHeadFacePath = ""; }
    
void IPlatformEnterPlatformCenter() {}
    
void IPlatformPay(const char* order, Chest chest) {}

void IPlatformClear() { strHeadFacePath = ""; }
    
void IPlatformInitHeadFace()
{
    ImagePickerController* controller = [[ImagePickerController alloc] initWithNibName:nil bundle:nil];
    if(controller != nil)
    {
        [controller autorelease];

        UIViewController *viewController = [[[UIApplication sharedApplication] keyWindow] rootViewController];
            
        if(viewController != nil)
        {
            [viewController.view addSubview:controller.view];
            [viewController addChildViewController:controller];
                
            [controller choosePhoto];
        }
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

void IPlatformUploadHeadFace(const char* updateUrl, const char* filePath, const char* guid) {}
