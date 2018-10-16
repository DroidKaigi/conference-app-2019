package io.github.droidkaigi.confsched2019.model

import com.soywiz.klock.DateTime

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
        val level: Level,
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

    val isFinished: Boolean
        get() = DateTime.now().unix > endTime.unix

    val isOnGoing: Boolean
        get() = DateTime.now().unix in startTime.unix..endTime.unix
}
