package com.mendix.developerapp.util

import android.os.Bundle

/**
 * Returns a copy of this bundle holding only the values React Native can turn into initial props,
 * dropping everything else.
 *
 * Launch options come from the activity's intent extras, which we do not control. Launchers, work
 * profiles and notification trampolines add values such as [android.os.UserHandle] that
 * `Arguments.fromBundle` rejects with an IllegalArgumentException, crashing the project while it
 * starts up. Anything dropped here could never have reached JavaScript in the first place.
 *
 * React Native has no sanitizing converter of its own to reuse: both `Arguments.fromBundle` and
 * `Arguments.makeNativeMap` throw on the first value they cannot represent. Its own contract is
 * that the app builds this bundle itself -- `ReactActivityDelegate.getLaunchOptions` returns null
 * by default -- so nothing upstream expects foreign data. Its converters cannot be used to test
 * convertibility either, as they allocate a `WritableNativeMap` and so need the React runtime
 * loaded, which it is not at the point we navigate.
 *
 * The supported set therefore mirrors `Arguments.fromBundle` and `Arguments.fromArray` by hand
 * (react-native 0.84.1, `ReactAndroid/src/main/java/com/facebook/react/bridge/Arguments.kt`):
 * nulls, strings, numbers, booleans, nested bundles, and the primitive and string arrays they
 * understand. The rarer shapes they also accept (bundle arrays, lists) are dropped too, as intent
 * extras do not carry them. Worth re-checking against that file when React Native is upgraded.
 */
@Suppress("DEPRECATION")
fun Bundle.sanitizedForReactNative(): Bundle {
    val sanitized = Bundle()
    for (key in keySet()) {
        when (val value = get(key)) {
            null -> sanitized.putString(key, null)
            is String -> sanitized.putString(key, value)
            is Boolean -> sanitized.putBoolean(key, value)
            is Int -> sanitized.putInt(key, value)
            is Number -> sanitized.putDouble(key, value.toDouble())
            is Bundle -> sanitized.putBundle(key, value.sanitizedForReactNative())
            is IntArray -> sanitized.putIntArray(key, value)
            is FloatArray -> sanitized.putFloatArray(key, value)
            is DoubleArray -> sanitized.putDoubleArray(key, value)
            is BooleanArray -> sanitized.putBooleanArray(key, value)
            is Array<*> -> if (value.javaClass.componentType == String::class.java) {
                @Suppress("UNCHECKED_CAST")
                sanitized.putStringArray(key, value as Array<String?>)
            }
            else -> Unit // Not representable in JavaScript, so drop it rather than crash later.
        }
    }
    return sanitized
}
