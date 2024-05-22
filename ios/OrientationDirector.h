#import <React/RCTEventEmitter.h>

#ifdef RCT_NEW_ARCH_ENABLED
#import <RNOrientationDirectorSpec/RNOrientationDirectorSpec.h>

NS_ASSUME_NONNULL_BEGIN

@interface OrientationDirector : RCTEventEmitter <NativeOrientationDirectorSpec>

NS_ASSUME_NONNULL_END

#else
#import <React/RCTBridgeModule.h>

@interface OrientationDirector : RCTEventEmitter <RCTBridgeModule>
#endif

@property (nonatomic, assign) BOOL isJsListening;

+(UIInterfaceOrientationMask)getSupportedInterfaceOrientationsForWindow;

@end
