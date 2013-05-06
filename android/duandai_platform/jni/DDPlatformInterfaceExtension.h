#ifndef _DDPlatformInterfaceExtension_h
#define _DDPlatformInterfaceExtension_h
#include "data_module.h"
class Chest;
extern "C"
{
	extern bool DDPlatformInit();

    extern void DDPlatformPurchase(const char* order, Chest chest, const char* param, ...);
}


#endif