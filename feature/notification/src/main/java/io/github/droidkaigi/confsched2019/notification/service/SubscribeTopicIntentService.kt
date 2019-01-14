package io.github.droidkaigi.confsched2019.notification.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import io.github.droidkaigi.confsched2019.notification.NotificationChannelInfo
import io.github.droidkaigi.confsched2019.notification.R
import io.github.droidkaigi.confsched2019.notification.Topic
import io.github.droidkaigi.confsched2019.notification.notificationBuilder
import io.github.droidkaigi.confsched2019.timber.error
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import timber.log.debug

class SubscribeTopicIntentService : IntentService(NAME) {
    override fun onHandleIntent(intent: Intent?) {
        try {
            if (NEED_FOREGROUND) {
                startForeground(
                    NAME.hashCode(),
                    notificationBuilder(NotificationChannelInfo.SUBSCRIBE_TOPIC).apply {
                        setContentTitle("Sample title") // FIXME
                        setContentText("Sample text")
                        setSmallIcon(R.mipmap.notification_icon)
                        setLocalOnly(false)
                    }.build()
                )
            }

            intent ?: return
            val topicName = "test_topic" ?: intent.getStringExtra(KEY_TOPIC_NAME) ?: return

            runBlocking {
                FirebaseInstanceId.getInstance().instanceId.await()

                Timber.debug { "Found a token so proceed to subscribe the topic" }

                FirebaseMessaging.getInstance().subscribeToTopic(topicName).await()

                Timber.debug { "Subscribed $topicName successfully" }
            }
        } catch (th: Throwable) {
            Timber.error(th)
        } finally {
            if (NEED_FOREGROUND) {
                // Don't need to use ServiceCompat in this branch
                stopForeground(true)
            }
        }
    }

    companion object {
        private const val NAME = "SubscribeTopicIntentService"
        private const val KEY_TOPIC_NAME = "key.topicName"

        private val NEED_FOREGROUND: Boolean
            get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

        fun start(context: Context, topic: Topic) {
            ContextCompat.startForegroundService(
                context,
                Intent(context, SubscribeTopicIntentService::class.java)
                    .putExtra(KEY_TOPIC_NAME, topic.name)
            )
        }
    }
}
