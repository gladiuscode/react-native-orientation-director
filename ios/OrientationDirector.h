#import <React/RCTEventEmitter.h>
#import <RNOrientationDirectorSpec/RNOrientationDirectorSpec.h>

NS_ASSUME_NONNULL_BEGIN

@interface OrientationDirector : RCTEventEmitter <NativeOrientationDirectorSpec>

@property (nonatomic, assign) BOOL isJsListening;

@end

NS_ASSUME_NONNULL_END
