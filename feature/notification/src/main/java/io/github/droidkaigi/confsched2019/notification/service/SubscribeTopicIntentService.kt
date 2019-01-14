package io.github.droidkaigi.confsched2019.notification.service

import android.app.IntentService
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.messaging.FirebaseMessaging
import io.github.droidkaigi.confsched2019.timber.error
import timber.log.Timber
import timber.log.debug

class SubscribeTopicIntentService : IntentService(NAME) {
    override fun onHandleIntent(intent: Intent?) {
        try {
            if (NEED_FOREGROUND) {
                startForeground(NAME.hashCode(), buildNotification())
            }

            intent ?: return

            val topicName = "test_topic"

            val currentThreadExecutor = currentThreadExecutor

            FirebaseMessaging.getInstance().subscribeToTopic(topicName)
                .addOnSuccessListener(currentThreadExecutor, OnSuccessListener {
                    Timber.debug { "Subscribed $topicName successfully" }
                })
                .addOnFailureListener(currentThreadExecutor, OnFailureListener {
                    Timber.debug { "Failed to subscribe $topicName" }
                    Timber.error(it)
                })
        } catch (th: Throwable) {
            Timber.error(th)
        } finally {
            if (NEED_FOREGROUND) {
                // Don't need to use ServiceCompat in this branch
                stopForeground(true)
            }
        }
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, "").apply {
            setContentTitle("")
            setContentText("The current endpoint is not for production")
            setSmallIcon(0)
            setLocalOnly(false)
        }.build()
    }

    companion object {
        private const val NAME = "SubscribeTopicIntentService"

        private val NEED_FOREGROUND: Boolean
            get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

        fun start(context: Context) {
            ContextCompat.startForegroundService(
                context,
                Intent(context, SubscribeTopicIntentService::class.java)
            )
        }
    }
}
