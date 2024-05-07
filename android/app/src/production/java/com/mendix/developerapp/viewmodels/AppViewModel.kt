package com.mendix.developerapp.viewmodels

import android.widget.CompoundButton
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

class AppViewModel : ViewModel() {

    val appUrl = MutableLiveData<String>()
    val clearData = MutableLiveData(false)
    val devModeEnabled = MutableLiveData(false)
    val appUrlInvalid = MutableLiveData(false)
    val supportsAr = MutableLiveData(true)
    val arUnsupportedMessageShown = MutableLiveData(false)
    val arUnsupportedToastShown = MutableLiveData(false)
    val qrCodeInvalid = MutableLiveData(false)

    var shouldShowValidationError = MutableLiveData(false)

    init {
        appUrl.observeForever {
            validateAppUrl(it)
        }
    }

    fun setup(
        appUrl: String,
        clearData: Boolean,
        devModeEnabled: Boolean,
        supportsAr: Boolean,
        arUnsupportedMessageShown: Boolean
    ) {
        this.clearData.value = clearData
        this.devModeEnabled.value = devModeEnabled
        this.appUrl.value = appUrl
        this.supportsAr.value = supportsAr
        this.arUnsupportedMessageShown.value = arUnsupportedMessageShown;
    }

    private fun validateAppUrl(appUrl: String) {
        val url = toUrl(appUrl.trim { it <= ' ' })
        appUrlInvalid.value = (url == null || url.querySize > 0 || url.encodedPath != "/")
    }

    private fun toUrl(url: String): HttpUrl? = url.let {
        (if (!it.matches(Regex("https?://.*"))) "http://$it" else it).toHttpUrlOrNull()
    }

    fun devChangedListener(view: CompoundButton, enabled: Boolean) {
        devModeEnabled.value = enabled
    }

    fun clearDataChangedListener(view: CompoundButton, enabled: Boolean) {
        clearData.value = enabled
    }

    fun onRuntimeTextInputChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        shouldShowValidationError.value = true
    }

    fun onARUnsupportedMessageShown() {
        arUnsupportedMessageShown.value = true
    }

    fun onARUnsupportedToastMessageShown() {
        arUnsupportedToastShown.value = true
    }

    fun setUrlFromQrScanner(url: String) {
        qrCodeInvalid.value = false
        if (url == "") {
            qrCodeInvalid.value = true
            return
        }
        appUrl.value = url
        qrCodeInvalid.value = appUrlInvalid.value
        appUrl.value = if (appUrlInvalid.value == false) appUrl.value else ""
    }
}
