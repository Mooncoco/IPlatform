//
//  DBSignupViewController.h
//  DBSignup
//
//  Created by Davide Bettio on 7/4/11.
//  Copyright 2011 03081340121. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "cocos2d.h"

using namespace cocos2d;

@interface ImagePickerController : UIViewController <UIActionSheetDelegate,
                                                    UIImagePickerControllerDelegate,
                                                    UINavigationControllerDelegate>
{
@private
    UIPopoverController* popoverController;
    NSMutableData* _receivedData;
}

@property (nonatomic, retain) UIPopoverController *popoverController;

- (void) choosePhoto;

@end
