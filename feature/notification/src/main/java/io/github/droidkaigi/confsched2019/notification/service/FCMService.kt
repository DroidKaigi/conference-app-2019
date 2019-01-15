package io.github.droidkaigi.confsched2019.notification.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.os.bundleOf
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.github.droidkaigi.confsched2019.ext.android.queryIntentAllActivities
import io.github.droidkaigi.confsched2019.notification.NotificationChannelInfo
import io.github.droidkaigi.confsched2019.notification.NotificationUtil
import io.github.droidkaigi.confsched2019.notification.createDefaultNotificationChannel

class FCMService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        // Need to have channels before receiving channels
        // So here is a good timing to create them
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            enumValues<NotificationChannelInfo>().forEach {
                createDefaultNotificationChannel(this, it)
            }
        }

        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(ACTION_TOKEN_PUBLISHED))
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        when (val notification = remoteMessage.notification) {
            null -> {
                handleDataNotification(remoteMessage, remoteMessage.data)
            }
            else -> {
                handleMessageNotification(remoteMessage, notification, remoteMessage.data)
            }
        }
    }

    private fun handleMessageNotification(
        remoteMessage: RemoteMessage,
        notification: RemoteMessage.Notification,
        data: Map<String, Any>
    ) {
        // drop other attributes for now
        NotificationUtil.showNotification(
            this,
            requireNotNull(notification.title),
            requireNotNull(notification.body),
            getPendingIntent(this, notification, data),
            // android_channel_id cannot be retrieved from fcm sdk so the server sends it in the data
            NotificationChannelInfo.of((data[KEY_CHANNEL_ID] as? String).orEmpty())
        )
    }

    private fun handleDataNotification(
        remoteMessage: RemoteMessage,
        data: Map<String, Any>
    ) {
        // no-op
    }

    private fun getPendingIntent(
        context: Context,
        notification: RemoteMessage.Notification,
        data: Map<String, Any>
    ): PendingIntent? {
        val options = bundleOf(
            *data.map { it.key to it.value }.toTypedArray()
        )

        if (notification.clickAction != null) {
            val intent = Intent(notification.clickAction)

            if (context.packageManager.queryIntentAllActivities(intent).isNotEmpty()) {
                return PendingIntent.getActivity(this, 0, intent, 0, options)
            }
        }

        if (notification.link != null) {
            val intent = Intent(Intent.ACTION_VIEW).setData(notification.link)

            if (context.packageManager.queryIntentAllActivities(intent).isNotEmpty()) {
                return PendingIntent.getActivity(this, 0, intent, 0, options)
            }
        }

        return null
    }

    companion object {
        const val ACTION_TOKEN_PUBLISHED = "action.tokenPublished"

        // The server side relies on this value
        private const val KEY_CHANNEL_ID = "channel_id"
    }
}
