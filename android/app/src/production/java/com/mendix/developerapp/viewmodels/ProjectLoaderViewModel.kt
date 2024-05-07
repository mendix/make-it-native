package com.mendix.developerapp.viewmodels

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.facebook.react.devsupport.interfaces.DevBundleDownloadListener
import com.mendix.developerapp.R
import com.mendix.mendixnative.BuildConfig
import com.mendix.mendixnative.api.ResponseStatus
import com.mendix.mendixnative.api.RuntimeInfoResponse
import com.mendix.mendixnative.api.getRuntimeInfo
import com.mendix.mendixnative.react.MxConfiguration
import com.mendix.mendixnative.util.MendixBackwardsCompatUtility

class ProjectLoaderViewModel : ViewModel() {
    private val mainHandler = Handler(Looper.getMainLooper())

    companion object {
        const val STATUS_LOADING = "status_loading"
        const val STATUS_ERROR_PACKAGER_CONNECTION = "status_error_packager"
        const val STATUS_ERROR_BUNDLE = "status_error_bundle"
        const val STATUS_ERROR_STUDIO_OUTDATED = "status_error_studio_outdated"
        const val STATUS_ERROR_MIN_OUTDATED = "status_error_min_outdated"
        const val STATUS_ERROR_CONNECTION = "status_error_connection"
        const val STATUS_ERROR_NO_NATIVE_PROFIlE = "status_error_no_native_profile"
        const val STATUS_SUCCESS = "status_success"
    }

    val status = MutableLiveData(STATUS_LOADING)
    val loading = MutableLiveData(true)
    val hasError = MutableLiveData(false)
    val hasProgress = MutableLiveData(false)
    val preChecksPassed = MutableLiveData(false)
    val errorMessageId = MutableLiveData(R.string.error_unknown)
    val canRetry = MutableLiveData(false)
    val canTestConnection = MutableLiveData(false)
    val downloadProgress = MutableLiveData(0)

    private fun updateErrorMessageForStatus() {
        errorMessageId.value = when (status.value) {
            STATUS_ERROR_BUNDLE -> R.string.error_js_bundle
            STATUS_ERROR_CONNECTION -> R.string.error_runtime_connection
            STATUS_ERROR_MIN_OUTDATED -> R.string.error_min_outdated
            STATUS_ERROR_STUDIO_OUTDATED -> R.string.error_studio_outdated
            STATUS_ERROR_NO_NATIVE_PROFIlE -> R.string.error_missing_native_profile
            STATUS_ERROR_PACKAGER_CONNECTION -> R.string.error_packager_not_running
            else -> R.string.error_unknown
        }
    }

    private fun updateCanRetry() {
        canRetry.value = status.value == STATUS_ERROR_CONNECTION || status.value == STATUS_ERROR_NO_NATIVE_PROFIlE
    }

    private fun updateTestConnection() {
        canTestConnection.value = status.value == STATUS_ERROR_CONNECTION
    }

    private fun setStatus(status: String) {
        postOnMainHandler {
            this.status.value = status
            hasError.value = (this.status.value == STATUS_LOADING || this.status.value == STATUS_SUCCESS).not()
            loading.value = this.status.value === STATUS_LOADING
            updateTestConnection()
            updateErrorMessageForStatus()
            updateCanRetry()
        }
    }

    fun runPreChecks(appUrl: String) {
        setStatus(STATUS_LOADING)
        getRuntimeInfo(appUrl) { runtimeInfoResponse: RuntimeInfoResponse ->
            when (runtimeInfoResponse.responseStatus) {
                ResponseStatus.INACCESSIBLE -> setStatus(STATUS_ERROR_CONNECTION)
                ResponseStatus.FAILED -> setStatus(STATUS_ERROR_CONNECTION)
                else -> {
                    val nativeBinaryVersion = runtimeInfoResponse.data!!.nativeBinaryVersion
                    MendixBackwardsCompatUtility.update(runtimeInfoResponse.data!!.version)
                    val supportedBinaryVersion = MxConfiguration.NATIVE_BINARY_VERSION
                    when {
                        nativeBinaryVersion == supportedBinaryVersion -> {
                            postOnMainHandler {
                                preChecksPassed.value = true
                            }
                        }
                        nativeBinaryVersion == null -> {
                            setStatus(STATUS_ERROR_NO_NATIVE_PROFIlE)
                        }
                        nativeBinaryVersion > supportedBinaryVersion -> {
                            setStatus(STATUS_ERROR_MIN_OUTDATED)
                        }
                        else -> {
                            setStatus(STATUS_ERROR_STUDIO_OUTDATED)
                        }
                    }
                }
            }
        }
    }

    val devServerCallback = object : DevBundleDownloadListener {
        override fun onSuccess() {
            setStatus(STATUS_SUCCESS)
        }

        override fun onProgress(text: String?, done: Int?, total: Int?) {
            setStatus(STATUS_LOADING)
            postOnMainHandler {
                hasProgress.value = true
                downloadProgress.value = ((done ?: 0).toDouble() / (total
                        ?: 1).toDouble() * 100).toInt()
            }
        }

        override fun onFailure(cause: Exception?) {
            setStatus(STATUS_ERROR_BUNDLE)
        }
    }

    private fun postOnMainHandler(cb: () -> Unit) {
        mainHandler.post {
            cb.invoke()
        }
    }
}
