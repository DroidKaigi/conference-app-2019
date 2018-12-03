package io.github.droidkaigi.confsched2019.model

import com.soywiz.klock.DateTime
import com.soywiz.klock.TimeSpan

sealed class Session(
    open val id: String,
    open val dayNumber: Int,
    open val startTime: DateTime,
    open val endTime: DateTime
) {
    data class SpeechSession(
        override val id: String,
        override val dayNumber: Int,
        override val startTime: DateTime,
        override val endTime: DateTime,
        val title: String,
        val desc: String,
        val room: Room,
        val format: String,
        val language: String,
        val topic: Topic,
        val isFavorited: Boolean,
        val speakers: List<Speaker>,
        val message: SessionMessage?
    ) : Session(id, dayNumber, startTime, endTime)

    data class SpecialSession(
        override val id: String,
        override val dayNumber: Int,
        override val startTime: DateTime,
        override val endTime: DateTime,
        val title: Int,
        val room: Room?
    ) : Session(id, dayNumber, startTime, endTime)

    val startDayText
        get() = startTime.format("yyyy.M.d")

    val isFinished: Boolean
        get() = DateTime.nowUnixLong() > endTime.unixMillisLong

    val isOnGoing: Boolean
        get() = DateTime.nowUnixLong() in startTime.unixMillisLong..endTime.unixMillisLong

    val timeInMinutes: Int
        get() = TimeSpan(endTime.unixMillis - startTime.unixMillis).minutes.toInt()
}
