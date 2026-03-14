#import "OrientationDirector.h"

/*
 This condition is needed to support use_frameworks.
 https://github.com/callstack/react-native-builder-bob/discussions/412#discussioncomment-6352402
 */
#if __has_include("OrientationDirector-Swift.h")
#import "OrientationDirector-Swift.h"
#else
#import "OrientationDirector/OrientationDirector-Swift.h"
#endif

static OrientationDirectorImpl *_director = SharedOrientationDirectorImpl.shared;

///////////////////////////////////////////////////////////////////////////////////////
///         EVENT EMITTER SETUP
///https://github.com/react-native-community/RNNewArchitectureLibraries/tree/feat/swift-event-emitter
@interface OrientationDirector() <OrientationEventEmitterDelegate>
@end
///
//////////////////////////////////////////////////////////////////////////////////////////

@implementation OrientationDirector

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

- (void)getInterfaceOrientation:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject
{
    dispatch_async(dispatch_get_main_queue(), ^{
        resolve(@([_director getInterfaceOrientation]));
    });
}

- (void)getDeviceOrientation:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject
{
    dispatch_async(dispatch_get_main_queue(), ^{
        resolve(@([_director getDeviceOrientation]));
    });
}

- (NSNumber *)isLocked
{
    return @([_director getIsLocked]);
}

- (void)lockTo:(double)jsOrientation
{
    NSNumber *jsOrientationNumber = @(jsOrientation);
    dispatch_async(dispatch_get_main_queue(), ^{
        [_director lockToJsValue:jsOrientationNumber];
    });
}

- (void)unlock
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [_director unlock];
    });
}

- (void)resetSupportedInterfaceOrientations
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [_director resetSupportedInterfaceOrientations];
    });
}

///////////////////////////////////////////////////////////////////////////////////////
///         STUBS
///

- (NSNumber *)isAutoRotationEnabled
{
    return @(NO);
}

- (void)disableOrientationSensors {}


- (void)enableOrientationSensors {}

///
///////////////////////////////////////////////////////////////////////////////////////

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeOrientationDirectorSpecJSI>(params);
}

+ (NSString *)moduleName
{
  return @"OrientationDirector";
}

@end
