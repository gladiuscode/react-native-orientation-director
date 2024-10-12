#import "OrientationDirector.h"

/*
 This condition is needed to support use_frameworks.
 https://github.com/callstack/react-native-builder-bob/discussions/412#discussioncomment-6352402
 */
#if __has_include("react_native_orientation_director-Swift.h")
#import "react_native_orientation_director-Swift.h"
#else
#import "react_native_orientation_director/react_native_orientation_director-Swift.h"
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

+ (BOOL)requiresMainQueueSetup
{
    return YES;
}

+ (UIInterfaceOrientationMask)getSupportedInterfaceOrientationsForWindow
{
    return [_director supportedInterfaceOrientations];
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
    return [EventManager supportedEvents];
}

- (void)sendEventWithName:(NSString * _Nonnull)name params:(NSDictionary *)params {
    [self sendEventWithName:name body:params];
}
///
///////////////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////////////
///         EXPORTED METHODS
///
RCT_EXPORT_METHOD(getInterfaceOrientation:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    dispatch_async(dispatch_get_main_queue(), ^{
        resolve(@([_director getInterfaceOrientation]));
    });
}

RCT_EXPORT_METHOD(getDeviceOrientation:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    dispatch_async(dispatch_get_main_queue(), ^{
        resolve(@([_director getDeviceOrientation]));
    });
}

RCT_EXPORT_BLOCKING_SYNCHRONOUS_METHOD(isLocked)
{
    return @([_director getIsLocked]);
}

RCT_EXPORT_METHOD(lockTo:(double)jsOrientation)
{
    NSNumber *jsOrientationNumber = @(jsOrientation);
    dispatch_async(dispatch_get_main_queue(), ^{
        [_director lockToJsValue:jsOrientationNumber];
    });
}

RCT_EXPORT_METHOD(unlock)
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [_director unlock];
    });
}

RCT_EXPORT_METHOD(resetSupportedInterfaceOrientations)
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [_director resetSupportedInterfaceOrientations];
    });
}

/**
 This method is a pure stub since we cannot access auto rotation setting in iOS
 */
RCT_EXPORT_BLOCKING_SYNCHRONOUS_METHOD(isAutoRotationEnabled)
{
    return @(NO);
}
///
///////////////////////////////////////////////////////////////////////////////////////

// Don't compile this code when we build for the old architecture.
#ifdef RCT_NEW_ARCH_ENABLED
- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeOrientationDirectorSpecJSI>(params);
}
#endif

@end
