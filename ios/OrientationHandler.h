#import <React/RCTEventEmitter.h>

#ifdef RCT_NEW_ARCH_ENABLED
#import <RNOrientationHandlerSpec/RNOrientationHandlerSpec.h>

NS_ASSUME_NONNULL_BEGIN

@interface OrientationHandler : RCTEventEmitter <NativeOrientationHandlerSpec>

NS_ASSUME_NONNULL_END

#else
#import <React/RCTBridgeModule.h>

@interface OrientationHandler : RCTEventEmitter <RCTBridgeModule>
#endif

@property (nonatomic, assign) BOOL isJsListening;

+(UIInterfaceOrientationMask)getSupportedInterfaceOrientationsForWindow;

@end
