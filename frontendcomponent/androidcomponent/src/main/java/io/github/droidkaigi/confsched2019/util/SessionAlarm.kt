package io.github.droidkaigi.confsched2019.util

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import com.soywiz.klock.DateTimeSpan
import com.soywiz.klock.minutes
import io.github.droidkaigi.confsched2019.broadcastreceiver.NotificationBroadcastReceiver
import io.github.droidkaigi.confsched2019.model.Lang
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.widget.component.R
import javax.inject.Inject

class SessionAlarm @Inject constructor(private val app: Application) {
    fun toggleRegister(session: Session, lang: Lang) {
        if (session.isFavorited) {
            unregister(session, lang)
        } else {
            register(session, lang)
        }
    }

    private fun register(session: Session, lang: Lang) {
        val time = session.startTime.unixMillisLong.minus(NOTIFICATION_TIME_BEFORE_START_MILLS)

        if (System.currentTimeMillis() < time) {
            val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    time,
                    createAlarmIntent(session, lang)
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    time,
                    createAlarmIntent(session, lang)
                )
            }
        }
    }

    private fun unregister(session: Session, lang: Lang) {
        val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(createAlarmIntent(session, lang))
    }

    private fun createAlarmIntent(session: Session, lang: Lang): PendingIntent {
        val timezoneOffset = DateTimeSpan(hours = 9)
        val displaySTime = session.startTime.plus(timezoneOffset).format("HH:mm")
        val displayETime = session.endTime.plus(timezoneOffset).format("HH:mm")
        val sessionTitle = app.getString(
            R.string.notification_message_session_title,
            when (session) {
                is Session.SpeechSession -> session.title.getByLang(lang)
                is Session.ServiceSession -> session.title
            }
        )
        val sessionStartTime = app.getString(
            R.string.notification_message_session_start_time,
            displaySTime,
            displayETime,
            session.room.name
        )
        val title: String
        val text: String
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            title = app.getString(R.string.notification_title_session_start)
            text = sessionTitle + "\n" + sessionStartTime
        } else {
            title = sessionTitle
            text = sessionStartTime
        }
        val intent = NotificationBroadcastReceiver.createIntent(
            app,
            session.id,
            title,
            text,
            NotificationChannelInfo.FAVORITE_SESSION_START
        )
        return PendingIntent.getBroadcast(
            app,
            session.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    companion object {
        private val NOTIFICATION_TIME_BEFORE_START_MILLS = 10.minutes.millisecondsLong
    }
}
