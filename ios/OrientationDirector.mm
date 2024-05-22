#import "OrientationDirector.h"

/*
 This condition is needed to support use_frameworks.
 https://github.com/callstack/react-native-builder-bob/discussions/412#discussioncomment-6352402
 */
#if __has_include("react_native_orientation_director-Swift.h")
#import "react_native_orientation_director-Swift.h"
#else
#import "react_native_orientation_director/react_native_orientation_director-Swift"
#endif

static OrientationDirectorImpl *_director = [OrientationDirectorImpl new];

///////////////////////////////////////////////////////////////////////////////////////
///         EVENT EMITTER SETUP
///https://github.com/react-native-community/RNNewArchitectureLibraries/tree/feat/swift-event-emitter
@interface OrientationDirector() <OrientationEventEmitterDelegate>
@end
///
//////////////////////////////////////////////////////////////////////////////////////////

@implementation OrientationDirector
RCT_EXPORT_MODULE()

- (instancetype)init
{
    self = [super init];
    if (self) {
        ///////////////////////////////////////////////////////////////////////////////////////
        ///         EVENT EMITTER SETUP
        [_director setEventManagerWithDelegate:self];
        ///
        //////////////////////////////////////////////////////////////////////////////////////////
    }
    return self;
}

///////////////////////////////////////////////////////////////////////////////////////
///         EVENT EMITTER SETUP
///
-(void)startObserving {
    self.isJsListening = YES;
}

-(void)stopObserving {
    self.isJsListening = NO;
}

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
    return [_director supportedInterfaceOrientation];
}

#ifdef RCT_NEW_ARCH_ENABLED
- (void)getInterfaceOrientation:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject
#else
RCT_EXPORT_METHOD(getInterfaceOrientation:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
#endif
{
    dispatch_async(dispatch_get_main_queue(), ^{
        resolve(@([_director getInterfaceOrientation]));
    });
}

#ifdef RCT_NEW_ARCH_ENABLED
- (void)getDeviceOrientation:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject
#else
RCT_EXPORT_METHOD(getDeviceOrientation:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
#endif
{
    dispatch_async(dispatch_get_main_queue(), ^{
        resolve(@([_director getDeviceOrientation]));
    });
}

#ifdef RCT_NEW_ARCH_ENABLED
- (void)lockTo:(double)jsOrientation
#else
RCT_EXPORT_METHOD(lockTo:(double)jsOrientation)
#endif
{
    NSNumber *jsOrientationNumber = @(jsOrientation);
    dispatch_async(dispatch_get_main_queue(), ^{
        [_director lockToJsOrientation:jsOrientationNumber];
    });
}

#ifdef RCT_NEW_ARCH_ENABLED
- (void)unlock
#else
RCT_EXPORT_METHOD(unlock)
#endif
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [_director unlock];
    });
}

// Don't compile this code when we build for the old architecture.
#ifdef RCT_NEW_ARCH_ENABLED
- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeOrientationDirectorSpecJSI>(params);
}
#endif

@end
