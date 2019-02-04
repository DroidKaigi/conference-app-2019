package io.github.droidkaigi.confsched2019

import com.soywiz.klock.DateTime
import com.soywiz.klock.hours
import com.soywiz.klock.minutes
import io.github.droidkaigi.confsched2019.model.Category
import io.github.droidkaigi.confsched2019.model.Lang
import io.github.droidkaigi.confsched2019.model.LocaledString
import io.github.droidkaigi.confsched2019.model.Room
import io.github.droidkaigi.confsched2019.model.SpeechSession

private val startTime =
    DateTime.createAdjusted(2019, 2, 7, 10, 0).toOffsetUnadjusted(9.hours).utc

fun dummySpeechSessionData() = SpeechSession(
    id = "2",
    dayNumber = 1,
    startTime = startTime + 100.minutes,
    endTime = startTime + 120.minutes,
    title = LocaledString(
        "How to create DroidKaigi App",
        "How to create DroidKaigi English"
    ),
    desc = "2 Please tell me",
    room = Room(2, "Room 2"),
    format = "this is format2",
    lang = Lang.EN,
    category = Category(10, LocaledString("ツール", "Tool")),
    intendedAudience = "extream",
    videoUrl = "https://droidkaigi.jp/2019/#might_be_null",
    slideUrl = "https://droidkaigi.jp/2019/#might_be_null",
    isInterpretationTarget = true,
    isFavorited = true,
    speakers = listOf(),
    message = LocaledString("部屋移動", "room moved"),
    forBeginners = true
)
