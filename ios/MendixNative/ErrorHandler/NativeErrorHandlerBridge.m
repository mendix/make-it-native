#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(NativeErrorHandler, NSObject)

RCT_EXTERN_METHOD(handle:(NSString *)message stack:(NSArray<NSDictionary *> *)stackTrace)

@end
