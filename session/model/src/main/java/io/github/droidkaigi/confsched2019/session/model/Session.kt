package io.github.droidkaigi.confsched2019.session.model

import org.threeten.bp.Instant

sealed class Session(
        open val id: String,
        open val dayNumber: Int,
        open val startTime: Instant,
        open val endTime: Instant
) {
    data class SpeechSession(
            override val id: String,
            override val dayNumber: Int,
            override val startTime: Instant,
            override val endTime: Instant,
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
            override val startTime: Instant,
            override val endTime: Instant,
            val title: Int,
            val room: Room?
    ) : Session(id, dayNumber, startTime, endTime)

    val isFinished: Boolean
        get() = System.currentTimeMillis() > endTime.toEpochMilli()

    val isOnGoing: Boolean
        get() = System.currentTimeMillis() in startTime.toEpochMilli()..endTime.toEpochMilli()
}
