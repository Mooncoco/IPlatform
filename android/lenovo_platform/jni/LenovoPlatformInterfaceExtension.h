#ifndef _LenovoPlatformInterfaceExtension_h
#define _LenovoPlatformInterfaceExtension_h
#include "data_module.h"
class Chest;
extern "C"
{
	extern bool LenovoPlatformInit();

    extern void LenovoPlatformPurchase(const char* order, Chest chest, const char* param, ...);
}


#endif
