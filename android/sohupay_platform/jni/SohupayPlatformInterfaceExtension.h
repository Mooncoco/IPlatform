#ifndef _SohupayPlatformInterfaceExtension_h
#define _SohupayPlatformInterfaceExtension_h
#include "data_module.h"
class Chest;
extern "C"
{
	extern bool SohupayPlatformInit();

    extern void SohupayPlatformPurchase(const char* order, Chest chest, const char* cid, const char* orderTime, const char* payId, const char* pid, const char* smid, const char* sign);
}


#endif
