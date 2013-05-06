//
//  IDSPlatformHandler.h
//  zjh
//
//  Created by admin on 13-1-5.
//
//

#ifndef __zjh__IDSPlatformHandler__
#define __zjh__IDSPlatformHandler__

extern CCNode *pSender;
extern SEL_CallFuncN callFunc;

@interface IDSPlatformHandler : NSObject
{

}

+(IDSPlatformHandler*) sharedInstance;

+(void) destory;

@end

#endif /* defined(__zjh__IDSPlatformHandler__) */
