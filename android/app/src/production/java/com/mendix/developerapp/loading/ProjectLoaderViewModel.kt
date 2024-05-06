package com.mendix.developerapp.loading

import android.os.Handler
import android.os.Looper
import android.widget.FrameLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.facebook.react.devsupport.interfaces.DevBundleDownloadListener
import com.mendix.developerapp.R
import com.mendix.mendixnative.api.ResponseStatus
import com.mendix.mendixnative.api.RuntimeInfoResponse
import com.mendix.mendixnative.api.getRuntimeInfo
import com.mendix.mendixnative.react.MxConfiguration
import com.mendix.mendixnative.util.MendixBackwardsCompatUtility
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ProjectLoaderState (
    val appUrl: String = "http://runtime-url",
    val appPort: String = "8083",
    val status: String = "status_loading",
    val loading: Boolean = true,
    val hasError: Boolean = false,
    val hasProgress: Boolean = false,
    val preChecksPassed: Boolean = false,
    val errorTypeId: Int = R.string.error_type_unknown,
    val canRetry: Boolean = false,
    val canTestConnection: Boolean = false,
    val downloadProgress: Int = 0,
    val loadingScreenText: Int = R.string.app_launch_text_getting_ready,
    val rnFrameLayout: FrameLayout? = null
)

class ProjectLoaderViewModel : ViewModel() {
    private val mutableState = MutableStateFlow(ProjectLoaderState())
    val state: StateFlow<ProjectLoaderState> = mutableState.asStateFlow()

    private val mainHandler = Handler(Looper.getMainLooper())

    lateinit var onBack : () -> Unit
    lateinit var onRetryButtonPressed : () -> Unit
    lateinit var openInBrowser : (String) -> Unit

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
    val preChecksPassed = MutableLiveData(false)
    private val loadingScreenText = listOf(
        R.string.app_launch_text_getting_ready,
        R.string.app_launch_text_baking,
        R.string.app_launch_text_crafting,
        R.string.app_launch_text_starting,
    ).random()

    init {
        mutableState.update { it.copy(
                status = STATUS_LOADING,
                loadingScreenText = loadingScreenText,
                errorTypeId = R.string.error_type_unknown
            )
        }
    }

    fun setRNFrameLayout(frameLayout: FrameLayout) {
        mutableState.update { it.copy(
                rnFrameLayout = frameLayout
            )
        }
    }

    fun setAppUrl(appUrl: String) {
        mutableState.update { it.copy(appUrl = appUrl) }
    }

    private fun updateErrorMessageForStatus() {
        val errorTypeId = when (status.value) {
            STATUS_ERROR_BUNDLE,
            STATUS_ERROR_CONNECTION,
            STATUS_ERROR_NO_NATIVE_PROFIlE,
            STATUS_ERROR_PACKAGER_CONNECTION -> R.string.error_type_connection

            STATUS_ERROR_STUDIO_OUTDATED,
            STATUS_ERROR_MIN_OUTDATED -> R.string.error_type_version

            else -> R.string.error_type_unknown
        }

        mutableState.update { it.copy(
                errorTypeId = errorTypeId
            )
        }
    }

    private fun updateCanRetry() {
        mutableState.update { it.copy(
                canRetry = status.value == STATUS_ERROR_CONNECTION || status.value == STATUS_ERROR_NO_NATIVE_PROFIlE,
            )
        }
    }

    private fun updateTestConnection() {
        mutableState.update { it.copy(
                canTestConnection = status.value == STATUS_ERROR_CONNECTION
            )
        }
    }

    private fun setStatus(status: String) {
        postOnMainHandler {
            this.status.value = status

            mutableState.update { it.copy(
                    status = status,
                    hasError = (this.status.value == STATUS_LOADING || this.status.value == STATUS_SUCCESS).not(),
                    loading = this.status.value === STATUS_LOADING
                )
            }

            updateTestConnection()
            updateErrorMessageForStatus()
            updateCanRetry()
        }
    }

    fun runPreChecks(checkAppUrl: String) {
        setStatus(STATUS_LOADING)
        getRuntimeInfo(checkAppUrl) { runtimeInfoResponse: RuntimeInfoResponse ->
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

                            mutableState.update { it.copy(
                                    preChecksPassed = preChecksPassed.value!!
                                )
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

            mutableState.update { it.copy(
                    hasProgress = true,
                    downloadProgress = ((done ?: 0).toDouble() / (total
                        ?: 1).toDouble() * 100).toInt()
                )
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
