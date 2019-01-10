package io.github.droidkaigi.confsched2019

import com.soywiz.klock.DateTime
import com.soywiz.klock.minutes
import io.github.droidkaigi.confsched2019.model.Category
import io.github.droidkaigi.confsched2019.model.LocaledString
import io.github.droidkaigi.confsched2019.model.Room
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.SessionType

private val startTime = DateTime.createAdjusted(2019, 2, 7, 10, 0)
fun dummySessionData(): List<Session> {
    return listOf(
        Session.ServiceSession(
            "0",
            1,
            startTime,
            startTime + 30.minutes,
            "session",
            Room(0, "Hall"),
            SessionType.WelcomeTalk,
            true
        ),
        firstDummySpeechSession(),
        Session.SpeechSession(
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
            language = LocaledString("英語", "English"),
            category = Category(10, LocaledString("ツール", "Tool")),
            intendedAudience = "extream",
            videoUrl = "https://droidkaigi.jp/2019/#might_be_null",
            slideUrl = "https://droidkaigi.jp/2019/#might_be_null",
            isInterpretationTarget = true,
            isFavorited = true,
            speakers = listOf(),
            message = LocaledString("部屋移動", "room moved")
        )
    )
}

fun firstDummySpeechSession(): Session.SpeechSession {
    return Session.SpeechSession(
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
        language = LocaledString("日本語", "Japanese"),
        category = Category(id = 10, name = LocaledString("アーキテクチャ", "App Architecture")),
        intendedAudience = "intermediate",
        videoUrl = "https://droidkaigi.jp/2019/#might_be_null",
        slideUrl = "https://droidkaigi.jp/2019/#might_be_null",
        isInterpretationTarget = false,
        isFavorited = false,
        speakers = listOf(),
        message = null
    )
}

