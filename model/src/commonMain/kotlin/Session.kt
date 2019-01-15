package io.github.droidkaigi.confsched2019.model

import com.soywiz.klock.DateTime
import com.soywiz.klock.DateTimeSpan
import com.soywiz.klock.TimeSpan
import com.soywiz.klock.TimezoneOffset

sealed class Session(
    open val id: String,
    open val dayNumber: Int,
    open val startTime: DateTime,
    open val endTime: DateTime,
    open val room: Room,
    open val isFavorited: Boolean
) {
    data class SpeechSession(
        override val id: String,
        override val dayNumber: Int,
        override val startTime: DateTime,
        override val endTime: DateTime,
        val title: LocaledString,
        val desc: String,
        override val room: Room,
        val format: String,
        val language: LocaledString,
        val category: Category,
        val intendedAudience: String?,
        val videoUrl: String?,
        val slideUrl: String?,
        val isInterpretationTarget: Boolean,
        override val isFavorited: Boolean,
        val speakers: List<Speaker>,
        val forBeginners: Boolean,
        val message: LocaledString?
    ) : Session(id, dayNumber, startTime, endTime, room, isFavorited) {
        val hasVideo: Boolean = videoUrl.isNullOrEmpty().not()
        val hasSlide: Boolean = slideUrl.isNullOrEmpty().not()
    }

    data class ServiceSession(
        override val id: String,
        override val dayNumber: Int,
        override val startTime: DateTime,
        override val endTime: DateTime,
        val title: LocaledString,
        val desc: String,
        override val room: Room,
        val sessionType: SessionType,
        override val isFavorited: Boolean
    ) : Session(id, dayNumber, startTime, endTime, room, isFavorited)

    val startDayText by lazy { startTime.format("yyyy.M.d") }

    fun timeSummary(lang: Lang, timezoneOffset: DateTimeSpan) = buildString {
        // ex: 2月2日 10:20-10:40
        if (lang == Lang.EN) {
            append(startTime.format("M"))
            append(".")
            append(startTime.format("d"))
        } else {
            append(startTime.format("M"))
            append("月")
            append(startTime.format("d"))
            append("日")
        }
        append(" ")
        append(startTime.plus(timezoneOffset).format("HH:mm"))
        append(" - ")
        append(endTime.plus(timezoneOffset).format("HH:mm"))
    }

    fun summary(lang: Lang, timezoneOffset: DateTimeSpan) = buildString {
        append(timeSummary(lang, timezoneOffset))
        append(" / ")
        append(timeInMinutes)
        append("min")
        append(" / ")
        append(room.name)
    }

    val isFinished: Boolean
        get() = DateTime.nowUnixLong() > endTime.unixMillisLong

    val isOnGoing: Boolean
        get() = DateTime.nowUnixLong() in startTime.unixMillisLong..endTime.unixMillisLong

    val timeInMinutes: Int
        get() = TimeSpan(endTime.unixMillis - startTime.unixMillis).minutes.toInt()
}
