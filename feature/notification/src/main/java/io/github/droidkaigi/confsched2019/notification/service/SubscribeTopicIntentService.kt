package io.github.droidkaigi.confsched2019.notification.service

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.IntentService
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.AlarmManagerCompat
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
import java.util.concurrent.TimeUnit

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

            val topicName =
                intent?.getStringExtra(KEY_TOPIC_NAME)?.takeIf { intent.isValid } ?: return

            runBlocking {
                FirebaseInstanceId.getInstance().instanceId.await()

                Timber.debug { "Found a token so proceed to subscribe the topic" }

                FirebaseMessaging.getInstance().subscribeToTopic(topicName).await()

                Timber.debug { "Subscribed $topicName successfully" }
            }
        } catch (th: Throwable) {
            Timber.error(th)

            if (intent?.isValid == true) {
                retrySelf(intent)
            }
        } finally {
            if (NEED_FOREGROUND) {
                // Don't need to use ServiceCompat in this branch
                stopForeground(true)
            }
        }
    }

    @SuppressLint("NewApi") // may be fixed by construct of Kotlin
    private fun retrySelf(intent: Intent) {
        val alarmManager = ContextCompat.getSystemService(this, AlarmManager::class.java) ?: let {
            Timber.debug { "Cannot retry myself due to missing alarm manager" }
            return
        }

        val pendingIntent = if (NEED_FOREGROUND) {
            PendingIntent.getForegroundService(this, REQUEST_CODE_RETRY_SELF, intent, 0)
        } else {
            PendingIntent.getService(this, REQUEST_CODE_RETRY_SELF, intent, 0)
        }

        val currentTry = intent.getIntExtra(KEY_RETRY_COUNT, 0)

        if (currentTry > MAX_RETRY_COUNT) {
            Timber.debug { "Retried $MAX_RETRY_COUNT times but could not subscribe the topic" }
            alarmManager.cancel(pendingIntent)
            return
        }

        intent.putExtra(KEY_RETRY_COUNT, currentTry + 1)

        AlarmManagerCompat.setAndAllowWhileIdle(
            alarmManager,
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1),
            pendingIntent
        )
    }

    private val Intent.isValid: Boolean
        get() = getStringExtra(KEY_TOPIC_NAME)?.isNotBlank() == true

    companion object {
        private const val NAME = "SubscribeTopicIntentService"
        private const val KEY_TOPIC_NAME = "KEY_TOPIC_NAME"
        private const val KEY_RETRY_COUNT = "KEY_RETRY_COUNT"
        private const val MAX_RETRY_COUNT = 3

        private const val REQUEST_CODE_RETRY_SELF = 10_230

        private inline val NEED_FOREGROUND: Boolean
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
