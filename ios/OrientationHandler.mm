#import "OrientationHandler.h"

/*
 This condition is needed to support use_frameworks.
 https://github.com/callstack/react-native-builder-bob/discussions/412#discussioncomment-6352402
 */
#if __has_include("react_native_orientation_handler-Swift.h")
#import "react_native_orientation_handler-Swift.h"
#else
#import "react_native_orientation_handler/react_native_orientation_handler-Swift"
#endif

static OrientationHandlerImpl *_handler = [OrientationHandlerImpl new];

///////////////////////////////////////////////////////////////////////////////////////
///         EVENT EMITTER SETUP
///https://github.com/react-native-community/RNNewArchitectureLibraries/tree/feat/swift-event-emitter
@interface OrientationHandler() <OrientationEventEmitterDelegate>
@end
///
//////////////////////////////////////////////////////////////////////////////////////////

@implementation OrientationHandler
RCT_EXPORT_MODULE()

- (instancetype)init
{
    self = [super init];
    if (self) {
        ///////////////////////////////////////////////////////////////////////////////////////
        ///         EVENT EMITTER SETUP
        [_handler setEventManagerDelegateWithDelegate:self];
        ///
        //////////////////////////////////////////////////////////////////////////////////////////
    }
    return self;
}

///////////////////////////////////////////////////////////////////////////////////////
///         EVENT EMITTER SETUP
///
- (NSArray<NSString *> *)supportedEvents {
    return [OrientationEventManager supportedEvents];
}

- (void)sendEventWithName:(NSString * _Nonnull)name params:(NSDictionary *)params {
    [self sendEventWithName:name body:params];
}
///
///////////////////////////////////////////////////////////////////////////////////////

+ (UIInterfaceOrientationMask)getSupportedInterfaceOrientationsForWindow
{
    return [_handler supportedInterfaceOrientation];
}

RCT_EXPORT_METHOD(getInterfaceOrientation:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    dispatch_async(dispatch_get_main_queue(), ^{
        resolve(@([_handler getInterfaceOrientation]));
    });
}

RCT_EXPORT_METHOD(lockTo:(nonnull NSNumber *)jsOrientation)
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [_handler lockToJsOrientation:jsOrientation];
    });
}

// Don't compile this code when we build for the old architecture.
#ifdef RCT_NEW_ARCH_ENABLED
- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeOrientationHandlerSpecJSI>(params);
}
#endif

@end
