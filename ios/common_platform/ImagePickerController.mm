//
//  DBSignupViewController.m
//  DBSignup
//
//  Created by Davide Bettio on 7/4/11.
//  Copyright 2011 03081340121. All rights reserved.
//
#import "stdafx.h"
#import "IPlatformEvent.h"
#import "IPlatformInterface.h"
#import "ImagePickerController.h"
#import "scene_controller.h"
#import "game_define.h"
#import "game_bdohttp_proto.h"

extern string strHeadFacePath;

// Safe releases
#define RELEASE_SAFELY(__POINTER) { [__POINTER release]; __POINTER = nil; }

@implementation ImagePickerController

@synthesize popoverController;


- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
}

- (void)dealloc
{
    RELEASE_SAFELY(popoverController)
    
    [super dealloc];
}

- (void)didReceiveMemoryWarning
{
    // Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
    
    // Release any cached data, images, etc that aren't in use.
}

#pragma mark - View lifecycle

// Override to allow orientations other than the default portrait orientation.
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
	return UIInterfaceOrientationIsLandscape( interfaceOrientation );
}

-(BOOL) shouldAutorotate { return YES; }

-(NSUInteger) supportedInterfaceOrientations { return UIInterfaceOrientationMaskLandscape; }

#pragma mark - IBActions

- (void)choosePhoto
{
    UIActionSheet *choosePhotoActionSheet;
    
    if([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera]) {
        choosePhotoActionSheet = [[UIActionSheet alloc] initWithTitle:NSLocalizedString(@"设置头像", @"")
                                                             delegate:self 
                                                    cancelButtonTitle:NSLocalizedString(@"取消", @"")
                                               destructiveButtonTitle:nil
                                                    otherButtonTitles:NSLocalizedString(@"马上照相", @""), NSLocalizedString(@"本地相册", @""), nil];
    } else {
        choosePhotoActionSheet = [[UIActionSheet alloc] initWithTitle:NSLocalizedString(@"设置头像", @"")
                                                             delegate:self 
                                                    cancelButtonTitle:NSLocalizedString(@"取消", @"")
                                               destructiveButtonTitle:nil
                                                    otherButtonTitles:NSLocalizedString(@"本地相册", @""), nil];
    }
    
    [choosePhotoActionSheet showInView:self.view];
//    [choosePhotoActionSheet release];
}



#pragma mark - UIActionSheetDelegate

- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex
{
	NSUInteger sourceType = 0;
    if([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera]) {
        switch (buttonIndex) {
            case 0:
                sourceType = UIImagePickerControllerSourceTypeCamera;
                break;
            case 1:
                sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
                break;
            case 2:
                return;
        }
    }
    else {
        if (buttonIndex == 1) {
            return;
        } else {
            sourceType = UIImagePickerControllerSourceTypeSavedPhotosAlbum;
        }
    }

    UIImagePickerController *imagePickerController = [[UIImagePickerController alloc] init];
    imagePickerController.delegate = self;
    imagePickerController.allowsEditing = YES;
    imagePickerController.sourceType = sourceType;
    
    //check if this device is iphone or ipad
    NSString *device = [UIDevice currentDevice].model;
    device = [device substringToIndex:4];
    
    if ([device isEqualToString:@"iPho"] || [device isEqualToString:@"iPod"])
    {
        // This is iPhone.
        
        if ([[UIDevice currentDevice].systemVersion floatValue] >= 6.0)
        {
            //for ios6
            [self presentViewController:imagePickerController animated:true completion:nil];
        }
        else
        {
            [self presentModalViewController:imagePickerController animated:YES];
        }
        
    }
    else if ([device isEqualToString:@"iPad"])
    {
        // This is iPad.
        
        UIPopoverController *popover = [[UIPopoverController alloc] initWithContentViewController:imagePickerController];
        self.popoverController = popover;
        [self.popoverController retain];
        
        [popoverController presentPopoverFromRect:CGRectMake(70, 270, 300, 300) inView:self.view permittedArrowDirections:UIPopoverArrowDirectionLeft animated:YES];
        
    }
    
}


#pragma mark - UIImagePickerControllerDelegate

- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info 
{
    if ([[UIDevice currentDevice].systemVersion floatValue] >= 6.0)
    {
        [picker dismissViewControllerAnimated:YES completion:nil];
    }
    else
    {
        [picker dismissModalViewControllerAnimated:YES];
    }
        
	UIImage* photo = [info objectForKey:UIImagePickerControllerEditedImage];
    
    // 写入到文件 png方式和jpeg方式
    
    // [UIImageJPEGRepresentation(image, 1.0f) writeToFile:[self findUniqueSavePath] atomically:YES];
    
    NSString* writeablePath = [NSString stringWithCString:CCFileUtils::getWriteablePath().c_str() encoding:NSUTF8StringEncoding];
    NSLog(@"writeablePath = %@", writeablePath);

    NSDate *date = [NSDate date];
    [[NSDate date] timeIntervalSince1970];
    
    NSString* path = [NSString stringWithFormat:@"%@%@", writeablePath, [date description], nil];
    
    NSLog(@"Final path = %@", path);

    [UIImagePNGRepresentation(photo) writeToFile: path atomically:YES];
    
    CCTexture2D *texture = CCTextureCache::sharedTextureCache()->addImage([path UTF8String]);
    
    if(getReachability())
    {
        [self uploadImageWithUrl:path];
        
        //save head texture key
        strHeadFacePath = [path UTF8String];
    }
    
    SceneController::GetInstancePtr()->PushEventToCurrent(kIPlatformEventPhotoNative, (void*)texture);

}


- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker
{
    if ([[UIDevice currentDevice].systemVersion floatValue] >= 6.0)
    {
        [picker dismissViewControllerAnimated:YES completion:nil];
    }
    else
    {
        [picker dismissModalViewControllerAnimated:YES];
    }
}

- (cocos2d::CCTexture2D*)convertToTexture2D:(UIImage *)_image
{
    CGImageRef imageRef = [_image CGImage];
    NSUInteger width = CGImageGetWidth(imageRef);
    NSUInteger height = CGImageGetHeight(imageRef);
    CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceRGB();
    unsigned char *rawData = (unsigned char*) calloc(height * width * 4, sizeof(unsigned char));
    NSUInteger bytesPerPixel = 4;
    NSUInteger bytesPerRow = bytesPerPixel * width;
    NSUInteger bitsPerComponent = 8;
    CGContextRef context = CGBitmapContextCreate(rawData, width, height,
                                                 bitsPerComponent, bytesPerRow, colorSpace,
                                                 kCGImageAlphaPremultipliedLast | kCGBitmapByteOrder32Big);
    CGColorSpaceRelease(colorSpace);
    CGContextDrawImage(context, CGRectMake(0, 0, width, height), imageRef);
    CGContextRelease(context);
    
    CCTexture2D *tempTexture = new CCTexture2D();
    tempTexture->autorelease();
    tempTexture->initWithData(rawData, kCCTexture2DPixelFormat_RGBA8888, width, height, CCSizeMake(width, height));
    
    return tempTexture;
}

-(void) uploadImageWithUrl:(NSString*)_path
{
    NSData* imgData = [NSData dataWithContentsOfFile:_path];
    NSMutableURLRequest* request = [NSMutableURLRequest requestWithURL:
                                            [NSURL URLWithString:@"http://fileupload.hiigame.com:8080/FileUploadControl"]
                                                                   cachePolicy:NSURLRequestReloadIgnoringLocalCacheData
                                                               timeoutInterval:10];
            
    [request setHTTPMethod:@"POST"];
    [request setValue:@"utf-8" forHTTPHeaderField:@"Charset"];
    [request setValue:@"keep-alive" forHTTPHeaderField:@"connection"];
            
    CFUUIDRef uuidObject = CFUUIDCreate(kCFAllocatorDefault);
    NSString *uuidStr = [(NSString *)CFUUIDCreateString(kCFAllocatorDefault, uuidObject) autorelease];
    NSString *content=[[NSString alloc]initWithFormat:@"multipart/form-data; boundary=%@",uuidStr];
    [request setValue:content forHTTPHeaderField:@"Content-Type"];
            
    NSMutableData *body = [NSMutableData data];
    [body appendData:[[NSString stringWithFormat:@"--%@\r\n",uuidStr] dataUsingEncoding:NSUTF8StringEncoding]];
    [body appendData:[@"Content-Disposition: form-data; name=\"img\"; filename=\"screenshot.png\"\r\n"  dataUsingEncoding:NSUTF8StringEncoding]];
    [body appendData:[@"Content-Type: application/octet-stream; charset=utf-8\r\n\r\n" dataUsingEncoding:NSUTF8StringEncoding]];
    [body appendData:imgData];
    [body appendData:[@"\r\n"  dataUsingEncoding:NSUTF8StringEncoding]];
    [body appendData:[[NSString stringWithFormat:@"--%@--\r\n", uuidStr] dataUsingEncoding:NSUTF8StringEncoding]];
    [request setHTTPBody:body];
            
    NSURLConnection *conn = [[NSURLConnection alloc] initWithRequest:request delegate:self];
    [conn retain];
            
    if(conn)
    {
        _receivedData = [[NSMutableData data] retain];
    }
}


-(void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response
{
    [_receivedData setLength:0];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
    [_receivedData appendData:data];
}

-(void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    NSString* result = [[NSString alloc] initWithData:_receivedData encoding:NSUTF8StringEncoding];
    
    Json::Reader reader;
	Json::Value root;
	bool flag = reader.parse([result UTF8String] ,root);
    
	if(flag)
	{
        string imgURL = root["imgUrl"].asString();
        
        NSLog(@"imgURL = %@", [NSString stringWithCString:imgURL.c_str() encoding:NSUTF8StringEncoding]);
        
        string strURL, strContent;
		GameBDOHttpProto::shareGameBDOHttpProto()->getUpdateFace(strURL, strContent, imgURL);
        
        NSString *postString =[NSString stringWithUTF8String:strContent.c_str()];
        NSURL *url = [NSURL URLWithString: [NSString stringWithUTF8String:strURL.c_str()]];
        NSMutableURLRequest *req = [NSMutableURLRequest requestWithURL:url];
        NSString *msgLength = [NSString stringWithFormat:@"%d", [postString length]];
        [req addValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-Type"];
        [req addValue:msgLength forHTTPHeaderField:@"Content-Length"];
        [req setHTTPMethod:@"POST"];
        [req setHTTPBody: [postString dataUsingEncoding:NSUTF8StringEncoding]];
        [[NSURLConnection alloc] initWithRequest:req delegate:nil];
        
        SceneController::GetInstancePtr()->PushEventToCurrent(kIPlatformEventPhotoUploadFinish, (void*)&imgURL);
	}
    else
    {
        SceneController::GetInstancePtr()->PushEventToCurrent(kIPlatformEventPhotoUploadFail);
        strHeadFacePath = "";
    }
    [_receivedData release]; 
}



@end
