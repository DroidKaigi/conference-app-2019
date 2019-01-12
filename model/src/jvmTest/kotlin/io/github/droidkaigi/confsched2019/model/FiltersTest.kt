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

    @Test fun isPass_WhenServiceSessionAndIsNotFilterable() {
        val serviceSession = mockk<Session.ServiceSession>()
        every { serviceSession.sessionType } returns SessionType.AfterParty
        assertTrue { Filters().isPass(serviceSession) }

        every { serviceSession.sessionType } returns SessionType.Lunch
        assertTrue { Filters().isPass(serviceSession) }
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

    @Test fun isPass_WhenFilterableServiceSessionAndRoomFiltered() {
        val room = Room(10, "room1")
        val serviceSession = mockk<Session.ServiceSession>()
        every { serviceSession.room } returns room
        every { serviceSession.sessionType } returns SessionType.Codelabs

        assertTrue { Filters(rooms = mutableSetOf(room)).isPass(serviceSession) }
    }

    @Test fun isPass_WhenCategoryFiltered() {
        val category = Category(10, LocaledString("ツール", "Tool"))
        val speechSession = mockk<Session.SpeechSession>()
        every { speechSession.category } returns category

        assertTrue { Filters(categories = mutableSetOf(category)).isPass(speechSession) }
    }

    @Test fun isPass_WhenCategoryFilteredDifferentCategory() {
        val category1 = Category(10, LocaledString("ツール1", "Tool1"))
        val category2 = Category(11, LocaledString("ツール2", "Tool2"))
        val speechSession = mockk<Session.SpeechSession>()
        every { speechSession.category } returns category1

        assertFalse { Filters(categories = mutableSetOf(category2)).isPass(speechSession) }
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
