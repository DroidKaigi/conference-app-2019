package io.github.droidkaigi.confsched2019.model

import com.soywiz.klock.DateTime
import com.soywiz.klock.DateTimeSpan
import com.soywiz.klock.TimeSpan

sealed class Session(
    open val id: String,
    open val dayNumber: Int,
    open val startTime: DateTime,
    open val endTime: DateTime,
    open val room: Room,
    open val isFavorited: Boolean
) {

    fun getStartDaytext(): String {
        return startTime.toOffset(Device.getOffsetFromUTC().timeSpan).format("yyyy.M.d")
    }

    fun timeSummary(lang: Lang, timezoneOffset: DateTimeSpan) = buildString {
        val startTimeTZ = startTime.toOffset(timezoneOffset.timeSpan)
        val endTimeTZ = endTime.toOffset(timezoneOffset.timeSpan)

        // ex: 2月2日 10:20-10:40
        if (lang == Lang.EN) {
            append(startTimeTZ.format("M"))
            append(".")
            append(startTimeTZ.format("d"))
        } else {
            append(startTimeTZ.format("M"))
            append("月")
            append(startTimeTZ.format("d"))
            append("日")
        }
        append(" ")
        append(startTimeTZ.format("HH:mm"))
        append(" - ")
        append(endTimeTZ.format("HH:mm"))
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

@AndroidParcelize
data class SpeechSession(
    override val id: String,
    override val dayNumber: Int,
    override val startTime: DateTime,
    override val endTime: DateTime,
    val title: LocaledString,
    val desc: String,
    override val room: Room,
    val format: String,
    val lang: Lang,
    val category: Category,
    val intendedAudience: String?,
    val videoUrl: String?,
    val slideUrl: String?,
    val isInterpretationTarget: Boolean,
    override val isFavorited: Boolean,
    val speakers: List<Speaker>,
    val forBeginners: Boolean,
    val message: LocaledString?
) : Session(id, dayNumber, startTime, endTime, room, isFavorited), AndroidParcel {
    val hasVideo: Boolean = videoUrl.isNullOrEmpty().not()
    val hasSlide: Boolean = slideUrl.isNullOrEmpty().not()
}

@AndroidParcelize
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
) : Session(id, dayNumber, startTime, endTime, room, isFavorited), AndroidParcel
