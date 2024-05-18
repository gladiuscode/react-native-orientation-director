#ifdef RCT_NEW_ARCH_ENABLED
#import "RNOrientationHandlerSpec.h"

@interface OrientationHandler : NSObject <NativeOrientationHandlerSpec>
#else
#import <React/RCTBridgeModule.h>

@interface OrientationHandler : NSObject <RCTBridgeModule>
#endif

+(UIInterfaceOrientationMask)getSupportedInterfaceOrientationsForWindow;

@end
