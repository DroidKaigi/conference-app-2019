package io.github.droidkaigi.confsched2019.broadcastreceiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.navigation.NavDeepLinkBuilder
import io.github.droidkaigi.confsched2019.notification.NotificationChannelInfo
import io.github.droidkaigi.confsched2019.notification.NotificationUtil.showNotification
import io.github.droidkaigi.confsched2019.session.ui.SessionDetailFragmentArgs
import io.github.droidkaigi.confsched2019.widget.component.R

// FIXME rename this
class NotificationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        intent ?: return
        val sessionId = intent.getStringExtra(EXTRA_SESSION_ID)
        val title = intent.getStringExtra(EXTRA_TITLE)
        val text = intent.getStringExtra(EXTRA_TEXT)
        val channelId = intent.getStringExtra(EXTRA_CHANNEL_ID)
        val pendingIntent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.navigation)
            .setDestination(R.id.session_detail)
            .setArguments(
                SessionDetailFragmentArgs.Builder(sessionId)
                    .build()
                    .toBundle()
            )
            .createTaskStackBuilder()
            .getPendingIntent(sessionId.hashCode(), PendingIntent.FLAG_UPDATE_CURRENT) ?: return
        showNotification(context, title, text, pendingIntent, channelId, R.mipmap.notification_icon)
    }

    companion object {
        private const val EXTRA_SESSION_ID = "EXTRA_SESSION_ID"
        private const val EXTRA_TITLE = "EXTRA_TITLE"
        private const val EXTRA_TEXT = "EXTRA_TEXT"
        private const val EXTRA_CHANNEL_ID = "EXTRA_CHANNEL_ID"
        fun createIntent(
            context: Context,
            sessionId: String,
            title: String,
            text: String,
            notificationChannelInfo: NotificationChannelInfo
        ): Intent {
            return Intent(context, NotificationBroadcastReceiver::class.java).apply {
                putExtra(EXTRA_SESSION_ID, sessionId)
                putExtra(EXTRA_TITLE, title)
                putExtra(EXTRA_TEXT, text)
                putExtra(EXTRA_CHANNEL_ID, notificationChannelInfo.channelId)
            }
        }
    }
}
