package io.github.droidkaigi.confsched2019.model

import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FiltersTest {
    @Test fun isPass_WhenNotFiltered() {
        assertTrue { Filters().isPass(mockk<Session.SpeechSession>()) }
    }

    @Test fun isPass_WhenSpecialSession() {
        assertTrue { Filters().isPass(mockk<Session.ServiceSession>()) }
    }

    @Test fun isPass_WhenRoomFiltered() {
        val room = Room(10, "room1")
        val speechSession = mockk<Session.SpeechSession>()
        every { speechSession.room } returns room

        assertTrue { Filters(rooms = mutableSetOf(room)).isPass(speechSession) }
    }

    @Test fun isPass_WhenRoomFilteredDifferentRoom() {
        val room1 = Room(10, "room1")
        val room2 = Room(11, "room2")
        val speechSession = mockk<Session.SpeechSession>()
        every { speechSession.room } returns room1

        assertFalse { Filters(rooms = mutableSetOf(room2)).isPass(speechSession) }
    }

    @Test fun isPass_WhenTopicFiltered() {
        val topic = Topic(10, "topic1")
        val speechSession = mockk<Session.SpeechSession>()
        every { speechSession.topic } returns topic

        assertTrue { Filters(topics = mutableSetOf(topic)).isPass(speechSession) }
    }

    @Test fun isPass_WhenTopicFilteredDifferentTopic() {
        val topic1 = Topic(10, "topic1")
        val topic2 = Topic(11, "topic2")
        val speechSession = mockk<Session.SpeechSession>()
        every { speechSession.topic } returns topic1

        assertFalse { Filters(topics = mutableSetOf(topic2)).isPass(speechSession) }
    }

    @Test fun isPass_WhenLangFiltered() {
        val lang = Lang.JA
        val speechSession = mockk<Session.SpeechSession>()
        every { speechSession.language } returns Lang.JA.text

        assertTrue { Filters(langs = mutableSetOf(lang)).isPass(speechSession) }
    }

    @Test fun isPass_WhenLangFilteredDifferentLang() {
        val lang1 = Lang.JA
        val lang2 = Lang.EN
        val speechSession = mockk<Session.SpeechSession>()
        every { speechSession.language } returns Lang.JA.text

        assertFalse { Filters(langs = mutableSetOf(lang2)).isPass(speechSession) }
    }
}
