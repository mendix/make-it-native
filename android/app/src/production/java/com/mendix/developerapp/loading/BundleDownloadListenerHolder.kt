package com.mendix.developerapp.loading

import com.facebook.react.devsupport.interfaces.DevBundleDownloadListener

/**
 * A delegating [DevBundleDownloadListener] that forwards calls to a mutable [delegate].
 *
 * This holder is set at ReactHost creation time (Application init). The actual delegate
 * is wired up later when the Fragment/ViewModel is created, and cleared on destroy.
 */
class BundleDownloadListenerHolder : DevBundleDownloadListener {
    var delegate: DevBundleDownloadListener? = null

    override fun onSuccess() {
        delegate?.onSuccess()
    }

    override fun onProgress(status: String?, done: Int?, total: Int?) {
        delegate?.onProgress(status, done, total)
    }

    override fun onFailure(cause: Exception) {
        delegate?.onFailure(cause)
    }
}
