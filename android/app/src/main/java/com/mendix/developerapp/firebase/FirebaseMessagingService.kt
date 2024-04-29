package com.mendix.developerapp.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.dieam.reactnativepushnotification.modules.RNPushNotificationListenerService
import com.google.firebase.messaging.RemoteMessage
import com.mendix.developerapp.R

const val MENDIX_AD_CAMPAIGN_CHANNEL = "MENDIX_AD_CAMPAIGN_CHANNEL"
const val MENDIX_AD_NOTIFICATION_ID = 9999

class FirebaseMessagingService: com.google.firebase.messaging.FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        handleMendixAdCampaignNotifications(this, remoteMessage)
    }

    private fun handleMendixAdCampaignNotifications(context: Context, message: RemoteMessage): Boolean {
        val notification = message.notification
        val isMendixAd = notification?.channelId?.trim() == MENDIX_AD_CAMPAIGN_CHANNEL
        if (notification != null && isMendixAd) {
            val data = message.data
            val action = data["action"]?.trim()

            val notificationBuilder = NotificationCompat
                .Builder(context, MENDIX_AD_CAMPAIGN_CHANNEL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notification.title)
                .setContentText(notification.body)
                .setAutoCancel(true)
            prepareIntent(context, action, data)?.let { intent ->
                notificationBuilder.setContentIntent(intent)
            }


            val notificationManager = context.getSystemService(RNPushNotificationListenerService.NOTIFICATION_SERVICE) as NotificationManager
            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(MENDIX_AD_CAMPAIGN_CHANNEL,
                    "Mendix advertising campaigns",
                    NotificationManager.IMPORTANCE_DEFAULT)
                notificationManager.createNotificationChannel(channel)
            }
            notificationManager.notify(MENDIX_AD_NOTIFICATION_ID, notificationBuilder.build())
            return true
        }
        return false
    }

    private fun prepareIntent(context: Context, action: String?, data: MutableMap<String, String>): PendingIntent? {
        if (action == "launch-url") {
            safeParseStringToUri(data["url"]?.trim())?.let {
                return@prepareIntent prepareBrowserIntent(context, it)
            }
        }
        return null
    }

    private fun prepareBrowserIntent(context: Context, uri: Uri): PendingIntent {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            this.data = uri
        }
        return PendingIntent.getActivity(context, MENDIX_AD_NOTIFICATION_ID, intent, PendingIntent.FLAG_ONE_SHOT)
    }

    private fun safeParseStringToUri(url: String?): Uri? {
        return try {
            Uri.parse(url)
        } catch(e: Exception) {
            Log.w("PushNotification", "Failed to parse notification URL.")
            null
        }
    }
}
