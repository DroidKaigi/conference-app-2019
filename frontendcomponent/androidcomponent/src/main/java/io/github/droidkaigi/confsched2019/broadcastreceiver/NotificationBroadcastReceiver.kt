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

class NotificationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return

        when (intent?.action) {
            ACTION_FAVORITED_SESSION_START -> showFavoritedSessionStartNotification(context, intent)
        }
    }

    private fun showFavoritedSessionStartNotification(context: Context, intent: Intent) {
        val sessionId = intent.getStringExtra(EXTRA_SESSION_ID)
        val title = intent.getStringExtra(EXTRA_TITLE)
        val text = intent.getStringExtra(EXTRA_TEXT)
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
        showNotification(
            context,
            title,
            text,
            pendingIntent,
            NotificationChannelInfo.FAVORITE_SESSION_START
        )
    }

    companion object {
        private const val ACTION_FAVORITED_SESSION_START = "ACTION_FAVORITED_SESSION_START"
        private const val EXTRA_SESSION_ID = "EXTRA_SESSION_ID"
        private const val EXTRA_TITLE = "EXTRA_TITLE"
        private const val EXTRA_TEXT = "EXTRA_TEXT"

        fun createForFavoritedSessionStart(
            context: Context,
            sessionId: String,
            title: String,
            text: String
        ): Intent {
            return Intent(context, NotificationBroadcastReceiver::class.java).apply {
                action = ACTION_FAVORITED_SESSION_START
                putExtra(EXTRA_SESSION_ID, sessionId)
                putExtra(EXTRA_TITLE, title)
                putExtra(EXTRA_TEXT, text)
            }
        }
    }
}
