#ifndef _AlipayPlatformInterfaceExtension_h
#define _AlipayPlatformInterfaceExtension_h
#include "data_module.h"
class Chest;
extern "C"
{
	extern bool AlipayPlatformInit();

	extern void AlipayPlatformPurchase(const char* order, Chest chest, const char* order_info, const char* sign);
}


#endif
