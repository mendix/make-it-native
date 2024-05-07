#import "objc_tryCatch.h"

NSException * _Nullable objc_tryCatch(void (^ _Nonnull block)(void)) {
    @try {
        block();
        return nil;
    } @catch (NSException *exception) {
        return exception;
    }
}
