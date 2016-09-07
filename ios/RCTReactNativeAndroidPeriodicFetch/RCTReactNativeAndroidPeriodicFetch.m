#import "RCTReactNativeAndroidPeriodicFetch.h"

@implementation ReactNativeAndroidPeriodicFetch

RCT_EXPORT_MODULE();

RCT_REMAP_METHOD(configure,
                 resolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject)
{
    resolve(@"Hello World!");
}

@end
