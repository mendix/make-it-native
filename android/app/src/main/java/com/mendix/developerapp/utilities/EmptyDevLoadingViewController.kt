package com.mendix.developerapp.utilities

import android.app.Activity
import android.view.View
import com.facebook.react.bridge.JavaJSExecutor
import com.facebook.react.bridge.JavaScriptExecutorFactory
import com.facebook.react.devsupport.DefaultDevLoadingViewImplementation
import com.facebook.react.devsupport.ReactInstanceDevHelper

class EmptyDevLoadingViewController() : DefaultDevLoadingViewImplementation(EmptyReactInstanceDevHelper()) {
    override fun showMessage(message: String?) {}
    override fun hide() {}
    override fun updateProgress(status: String?, done: Int?, total: Int?) {}
}

private class EmptyReactInstanceDevHelper : ReactInstanceDevHelper {
    override fun onReloadWithJSDebugger(proxyExecutorFactory: JavaJSExecutor.Factory?) {}

    override fun onJSBundleLoadedFromServer() {}

    override fun toggleElementInspector() {}

    override fun getCurrentActivity(): Activity? = null

    override fun getJavaScriptExecutorFactory(): JavaScriptExecutorFactory? = null

    override fun createRootView(appKey: String?): View? = null

    override fun destroyRootView(rootView: View?) {}
}
