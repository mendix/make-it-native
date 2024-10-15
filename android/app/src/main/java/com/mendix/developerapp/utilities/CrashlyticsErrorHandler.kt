package com.mendix.developerapp.utilities

import android.content.Context
import android.os.Bundle
import com.facebook.react.common.JavascriptException
import com.facebook.react.devsupport.StackTraceHelper
import com.facebook.react.devsupport.interfaces.StackFrame
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mendix.mendixnative.error.ErrorHandler
import com.mendix.mendixnative.error.ErrorType

class CrashlyticsErrorHandler(val context: Context) : ErrorHandler {
    override fun handleError(title: String?, stack: Array<out StackFrame>?, type: ErrorType) {
        FirebaseCrashlytics.getInstance().setCustomKey("Error type", type.name)
        FirebaseCrashlytics.getInstance().log(StackTraceHelper.formatStackTrace(title, stack!!))
        (if (type === ErrorType.NATIVE) Exception(title) else title?.let {
            JavascriptException(
                it
            )
        })?.let { FirebaseCrashlytics.getInstance().recordException(it) }
        // Testing out events as a possible way of tracking red boxes.
        Bundle().let {
            it.putString("title", title)
            it.putString("type", type.name)
            it.putString("stack_frame", StackTraceHelper.formatStackTrace(title, stack))
            FirebaseAnalytics.getInstance(context).logEvent("red_box", it)
        }
    }
}
