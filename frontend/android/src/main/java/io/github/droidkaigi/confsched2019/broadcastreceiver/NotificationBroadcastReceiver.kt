package io.github.droidkaigi.confsched2019.broadcastreceiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.navigation.NavDeepLinkBuilder
import io.github.droidkaigi.confsched2019.notification.NotificationChannelInfo
import io.github.droidkaigi.confsched2019.notification.NotificationUtil.showNotification
import io.github.droidkaigi.confsched2019.session.ui.SessionDetailFragmentArgs
import io.github.droidkaigi.confsched2019.util.SessionAlarm.Companion.ACTION_FAVORITED_SESSION_START
import io.github.droidkaigi.confsched2019.util.SessionAlarm.Companion.EXTRA_SESSION_ID
import io.github.droidkaigi.confsched2019.util.SessionAlarm.Companion.EXTRA_TEXT
import io.github.droidkaigi.confsched2019.util.SessionAlarm.Companion.EXTRA_TITLE
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
}
