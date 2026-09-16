package com.mendix.developerapp.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mendix.developerapp.history.History
import com.mendix.developerapp.util.HistoryManager
import com.mendix.mendixnative.config.AppPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import java.util.*

data class HomeState(
    val appUrl: String = "",
    val clearData: Boolean = false,
    val devModeEnabled: Boolean = false,
    val appUrlInvalid: Boolean = false,
    val supportsAr: Boolean = true,
    val arUnsupportedMessageShown: Boolean = false,
    val arUnsupportedToastShown: Boolean = false,
    val qrCodeInvalid: Boolean = false,
    val shouldShowValidationError: Boolean = false,
    val showQRCodeDialog: Boolean = false,
)

class HomeViewModel(application: Application): AndroidViewModel(application) {
    private val mutableState = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = mutableState.asStateFlow()

    private var historyManager: HistoryManager
    private var preferences: AppPreferences

    lateinit var launchApp : () -> Unit
    lateinit var qrCodeScannerButtonOnClick : () -> Unit

    init {
        historyManager = HistoryManager(application)
        preferences = AppPreferences(application)
        setAppUrl(preferences.appUrl)
        setClearData(true)
    }

    fun getAppUrl(): String {
        // Hand out the same string validateAppUrl checks. A trailing space or newline, easily
        // picked up from a scanned QR code, would otherwise pass validation and then break URL
        // parsing further down.
        return mutableState.value.appUrl.trim { it <= ' ' }
    }

    fun setAppUrl(value: String) {
        validateAppUrl(value)
        mutableState.update { currentState ->
            currentState.copy(appUrl = value)
        }
    }

    fun getClearData(): Boolean {
        return mutableState.value.clearData
    }

    fun setClearData(enabled: Boolean) {
        mutableState.update { currentState ->
            currentState.copy(clearData = enabled)
        }
    }

    fun getDevModeEnabled(): Boolean {
        return mutableState.value.devModeEnabled
    }

    fun setDevModeEnabled(enabled: Boolean) {
        mutableState.update { currentState ->
            currentState.copy(devModeEnabled = enabled)
        }
    }

    fun toggleQRCodeDialog(){
        mutableState.update { currentState->
            currentState.copy(showQRCodeDialog = !currentState.showQRCodeDialog)
        }
    }

    fun launchAppOnClickListener() {
        if (!mutableState.value.appUrlInvalid){
            preferences.appUrl = getAppUrl()
            launchApp.invoke()
        }
    }

    fun saveHistory(appUrl: String) {
        val history = History(
            url = appUrl,
            lastConnection = Date()
        )
        val historyList = historyManager.getHistoryList()
        val currentHistory = historyList.find { it.url == appUrl }
        if (currentHistory == null) {
            historyManager.addHistory(history)
        } else {
            historyManager.updateHistory(currentHistory, history.copy(isFavorite = currentHistory.isFavorite))
        }
    }

    fun setup(
        appUrl: String,
        clearData: Boolean,
        devModeEnabled: Boolean,
        supportsAr: Boolean,
        arUnsupportedMessageShown: Boolean
    ) {
        mutableState.update { currentState ->
            currentState.copy(
                appUrl = appUrl,
                clearData = clearData,
                devModeEnabled = devModeEnabled,
                supportsAr = supportsAr,
                arUnsupportedMessageShown = arUnsupportedMessageShown
            )
        }
    }

    private fun validateAppUrl(appUrl: String) {
        val url = toUrl(appUrl.trim { it <= ' ' })
        mutableState.update { currentState -> currentState.copy(
            appUrlInvalid = (url == null || url.querySize > 0 || url.encodedPath != "/")
        ) }
    }

    private fun toUrl(url: String): HttpUrl? = url.let {
        (if (!it.matches(Regex("https?://.*"))) "http://$it" else it).toHttpUrlOrNull()
    }

    fun onBarCodeScanSuccess(url: String){
        // Several frames are analysed concurrently, so the same QR code is usually reported more
        // than once. Only the first scan may launch: the launch action is declared on the home
        // destination, so navigating again after we left it throws. Dismissing the dialog below
        // makes this a one-shot until the scanner is opened again.
        if (!mutableState.value.showQRCodeDialog) return

        setAppUrl(url)
        toggleQRCodeDialog()
        launchApp()
    }
}
