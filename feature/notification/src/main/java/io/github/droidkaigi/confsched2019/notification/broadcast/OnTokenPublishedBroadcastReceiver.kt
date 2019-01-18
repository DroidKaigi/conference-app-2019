package io.github.droidkaigi.confsched2019.notification.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.github.droidkaigi.confsched2019.notification.service.FCMService

abstract class OnTokenPublishedBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return

        if (intent?.action == FCMService.ACTION_TOKEN_PUBLISHED) {
            onTokenPublished(context, intent)
        }
    }

    abstract fun onTokenPublished(context: Context, intent: Intent)
}
