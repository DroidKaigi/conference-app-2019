package io.github.droidkaigi.confsched2019.notification.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.os.bundleOf
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.github.droidkaigi.confsched2019.ext.android.queryIntentAllActivities
import io.github.droidkaigi.confsched2019.notification.NotificationChannelInfo
import io.github.droidkaigi.confsched2019.notification.NotificationUtil

class FCMService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
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
