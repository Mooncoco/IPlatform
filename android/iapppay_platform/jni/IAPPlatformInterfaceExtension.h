#ifndef _IAPPlatformInterfaceExtension_h
#define _IAPPlatformInterfaceExtension_h
#include "data_module.h"
class Chest;
extern "C"
{
	extern bool IAPPlatformInit();

    extern void IAPPlatformPurchase(const char* order, Chest chest, const char* param, ...);
}


#endif
