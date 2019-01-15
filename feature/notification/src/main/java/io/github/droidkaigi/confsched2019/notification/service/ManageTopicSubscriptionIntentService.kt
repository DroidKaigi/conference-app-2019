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

class ManageTopicSubscriptionIntentService : IntentService(NAME) {
    override fun onHandleIntent(intent: Intent?) {
        try {
            val topicsToBeSubscribed =
                intent?.getStringArrayExtra(KEY_TOPIC_NAMES_TO_BE_SUBSCRIBED).orEmpty()

            if (NEED_FOREGROUND) {
                // We need to make this foreground surely even if we wanna finish handling the intent immediately
                val title = getString(R.string.notification_foreground_subscribe_topic_title)
                val text = getString(
                    R.string.notification_foreground_subscribe_topic_text,
                    topicsToBeSubscribed.joinToString(", ")
                )

                startForeground(
                    NAME.hashCode(),
                    notificationBuilder(
                        channelInfo = NotificationChannelInfo.DEFAULT
                    ).apply {
                        setContentTitle(title)
                        setContentText(text)
                        setSmallIcon(R.drawable.ic_notification)
                        setLocalOnly(false)
                    }.build()
                )
            }

            if (!intent.isValid) {
                return
            }

            val topicsToBeUnsubscribed =
                intent?.getStringArrayExtra(KEY_TOPIC_NAMES_TO_BE_UNSUBSCRIBED).orEmpty()

            runBlocking {
                FirebaseInstanceId.getInstance().instanceId.await()

                Timber.debug { "Found a token so proceed to subscribe the topic" }

                topicsToBeSubscribed.forEach { topicName ->
                    FirebaseMessaging.getInstance().subscribeToTopic(topicName).await()

                    Timber.debug { "Subscribed $topicName successfully" }
                }

                topicsToBeUnsubscribed.forEach { topicName ->
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(topicName).await()

                    Timber.debug { "Unsubscribed $topicName successfully" }
                }
            }
        } catch (th: Throwable) {
            Timber.error(th)

            if (intent.isValid) {
                retrySelf(requireNotNull(intent))
            }
        } finally {
            if (NEED_FOREGROUND) {
                // Don't need to use ServiceCompat in this branch
                stopForeground(true)
            }
        }
    }

    private fun retrySelf(intent: Intent) {
        val alarmManager = ContextCompat.getSystemService(this, AlarmManager::class.java) ?: let {
            Timber.debug { "Cannot retry myself due to missing alarm manager" }
            return
        }

        val pendingIntent = createRetryPendingIntent(intent)

        val currentTry = intent.getIntExtra(KEY_RETRY_COUNT, 0)

        if (currentTry > MAX_RETRY_COUNT) {
            Timber.debug {
                "Retried $MAX_RETRY_COUNT times but could not handle any or all of given topics"
            }
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

    @SuppressLint("NewApi") // may be fixed by construct of Kotlin
    private fun Context.createRetryPendingIntent(intent: Intent): PendingIntent {
        return if (NEED_FOREGROUND) {
            PendingIntent.getForegroundService(this, REQUEST_CODE_RETRY, intent, 0)
        } else {
            PendingIntent.getService(this, REQUEST_CODE_RETRY, intent, 0)
        }
    }

    private val Intent?.isValid: Boolean
        get() = this != null &&
            (getStringArrayExtra(KEY_TOPIC_NAMES_TO_BE_SUBSCRIBED)?.isNotEmpty() == true ||
                getStringArrayExtra(KEY_TOPIC_NAMES_TO_BE_UNSUBSCRIBED)?.isNotEmpty() == true)

    companion object {
        private const val NAME = "ManageTopicSubscriptionIntentService"
        private const val KEY_TOPIC_NAMES_TO_BE_SUBSCRIBED = "KEY_TOPIC_NAMES_TO_BE_SUBSCRIBED"
        private const val KEY_TOPIC_NAMES_TO_BE_UNSUBSCRIBED = "KEY_TOPIC_NAMES_TO_BE_UNSUBSCRIBED"
        private const val KEY_RETRY_COUNT = "KEY_RETRY_COUNT"
        private const val MAX_RETRY_COUNT = 3

        private const val REQUEST_CODE_RETRY = 10_230

        private inline val NEED_FOREGROUND: Boolean
            get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

        fun start(
            context: Context,
            subscribes: List<Topic> = emptyList(),
            unsubscribes: List<Topic> = emptyList()
        ) {
            ContextCompat.startForegroundService(
                context,
                Intent(context, ManageTopicSubscriptionIntentService::class.java)
                    .putExtra(
                        KEY_TOPIC_NAMES_TO_BE_SUBSCRIBED,
                        subscribes.map { it.name }.toTypedArray()
                    )
                    .putExtra(
                        KEY_TOPIC_NAMES_TO_BE_UNSUBSCRIBED,
                        unsubscribes.map { it.name }.toTypedArray()
                    )
            )
        }
    }
}
