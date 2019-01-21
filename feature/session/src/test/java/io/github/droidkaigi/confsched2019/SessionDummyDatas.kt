package io.github.droidkaigi.confsched2019

import com.soywiz.klock.DateTime
import com.soywiz.klock.hours
import com.soywiz.klock.minutes
import io.github.droidkaigi.confsched2019.model.Category
import io.github.droidkaigi.confsched2019.model.Lang
import io.github.droidkaigi.confsched2019.model.LocaledString
import io.github.droidkaigi.confsched2019.model.Room
import io.github.droidkaigi.confsched2019.model.ServiceSession
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.SessionType
import io.github.droidkaigi.confsched2019.model.SpeechSession

private val startTime =
    DateTime.createAdjusted(2019, 2, 7, 10, 0).toOffsetUnadjusted(9.hours).utc

fun dummySessionData(): List<Session> {
    return listOf(
        ServiceSession(
            "0",
            1,
            startTime,
            startTime + 30.minutes,
            LocaledString("session", "session English"),
            "session desc",
            Room(0, "Hall"),
            SessionType.WELCOME_TALK,
            true
        ),
        firstDummySpeechSession(),
        SpeechSession(
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
    )
}

fun firstDummySpeechSession(): SpeechSession {
    return SpeechSession(
        id = "1",
        dayNumber = 1,
        startTime = startTime + 60.minutes,
        endTime = startTime + 90.minutes,
        title = LocaledString(
            "How to create DroidKaigi App2",
            "How to create DroidKaigi English 2"
        ),
        desc = "Please tell me",
        room = Room(1, "Room 1"),
        format = "this is format",
        lang = Lang.JA,
        category = Category(id = 10, name = LocaledString("アーキテクチャ", "App Architecture")),
        intendedAudience = "intermediate",
        videoUrl = "https://droidkaigi.jp/2019/#might_be_null",
        slideUrl = "https://droidkaigi.jp/2019/#might_be_null",
        isInterpretationTarget = false,
        isFavorited = false,
        speakers = listOf(),
        message = null,
        forBeginners = false
    )
}
