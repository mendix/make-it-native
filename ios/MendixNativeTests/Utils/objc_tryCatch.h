#import <Foundation/Foundation.h>

// Do not use in production code. It can leak context.
NSException * _Nullable objc_tryCatch(void (^ _Nonnull block)(void));
