package io.github.droidkaigi.confsched2019.notification.service

import com.google.firebase.messaging.FirebaseMessagingService

class FCMService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        SubscribeTopicIntentService.start(this)
    }
}
