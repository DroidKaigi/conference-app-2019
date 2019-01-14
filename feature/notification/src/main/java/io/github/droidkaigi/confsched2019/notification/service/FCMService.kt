package io.github.droidkaigi.confsched2019.notification.service

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService

class FCMService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(ACTION_TOKEN_PUBLISHED))
    }

    companion object {
        const val ACTION_TOKEN_PUBLISHED = "action.tokenPublished"
    }
}
