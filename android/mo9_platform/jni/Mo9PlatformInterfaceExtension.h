#ifndef _Mo9PlatformInterfaceExtension_h
#define _Mo9PlatformInterfaceExtension_h
#include "data_module.h"
class Chest;
extern "C"
{
	extern bool Mo9PlatformInit();

    extern void Mo9PlatformPurchase(const char* order, Chest chest, const char* param, ...);
}


#endif
