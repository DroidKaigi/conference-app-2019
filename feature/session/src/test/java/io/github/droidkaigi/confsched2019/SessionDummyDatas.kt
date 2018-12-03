package io.github.droidkaigi.confsched2019

import com.soywiz.klock.DateTime
import com.soywiz.klock.minutes
import io.github.droidkaigi.confsched2019.model.Room
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.SessionMessage
import io.github.droidkaigi.confsched2019.model.Topic

private val startTime = DateTime.createAdjusted(2019, 2, 7, 10, 0)
fun dummySessionData(): List<Session> {
    return listOf(
        Session.SpecialSession(
            "0",
            1,
            startTime,
            startTime + 30.minutes,
            0,
            Room(0, "Hall")
        ),
        firstDummySpeechSession(),
        Session.SpeechSession(
            id = "2",
            dayNumber = 1,
            startTime = startTime + 100.minutes,
            endTime = startTime + 120.minutes,
            title = "How to create DroidKaigi App2",
            desc = "2 Please tell me",
            room = Room(2, "Room 2"),
            format = "this is format2",
            language = "English",
            topic = Topic(10, "Tool"),
            isFavorited = true
            ,
            speakers = listOf(),
            message = SessionMessage("部屋移動", "room moved")
        )
    )
}

fun firstDummySpeechSession(): Session.SpeechSession {
    return Session.SpeechSession(
        id = "1",
        dayNumber = 1,
        startTime = startTime + 60.minutes,
        endTime = startTime + 90.minutes,
        title = "How to create DroidKaigi App",
        desc = "Please tell me",
        room = Room(1, "Room 1"),
        format = "this is format",
        language = "日本語",
        topic = Topic(id = 10, name = "App Architecture"),
        isFavorited = false,
        speakers = listOf(),
        message = null
    )
}

