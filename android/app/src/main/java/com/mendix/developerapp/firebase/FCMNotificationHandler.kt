package com.mendix.developerapp.firebase

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log

class FCMNotificationHandler(val context: Activity) {
    fun handleNotification(extras: Bundle) {
        when (extras.getString("action")?.trim()) {
            "launch-url" -> launchUrl(extras)
        }
    }

    private fun launchUrl(extras: Bundle) {
        safeParseStringToUri(extras.getString("url")?.trim())?.let {
            context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                this.data = it
            })
        }
    }

    private fun safeParseStringToUri(url: String?): Uri? {
        return try {
            Uri.parse(url)
        } catch (e: Exception) {
            Log.w("PushNotification", "Failed to parse notification URL.")
            null
        }
    }
}