package io.github.droidkaigi.confsched2019.model

import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.After
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FiltersTest {
    companion object {
        val serviceSession = mockk<ServiceSession>()
        val speechSession = mockk<SpeechSession>()
    }

    @After fun tearDown() {
        clearMocks(serviceSession, speechSession)
    }

    @Test fun isPass_WhenNotFiltered() {
        // setup
        val sut = Filters()

        // verify
        assertTrue { sut.isPass(speechSession) }
    }

    @Test fun isPass_When_AFTER_PERTY_IsNotFilterable() {
        // setup
        val sut = Filters()
        every { serviceSession.sessionType } returns SessionType.AFTER_PARTY

        // verity
        assertTrue { sut.isPass(serviceSession) }
    }

    @Test fun isPass_When_LUNCH_IsNotFilterable() {
        // setup
        val sut = Filters()
        every { serviceSession.sessionType } returns SessionType.LUNCH

        // verify
        assertTrue { sut.isPass(serviceSession) }
    }

    @Test fun isPass_WhenRoomFiltered() {
        // setup
        val room = Room(10, "room1")
        val sut = Filters(rooms = mutableSetOf(room))
        every { speechSession.room } returns room

        // verify
        assertTrue { sut.isPass(speechSession) }
    }

    @Test fun isPass_WhenRoomFilteredDifferentRoom() {
        // setup
        val room1 = Room(10, "room1")
        val room2 = Room(11, "room2")
        val sut = Filters(rooms = mutableSetOf(room2))
        every { speechSession.room } returns room1

        // verify
        assertFalse { sut.isPass(speechSession) }
    }

    @Test fun isPass_WhenFilterableServiceSessionAndRoomFiltered() {
        // setup
        val room = Room(10, "room1")
        val sut = Filters(rooms = mutableSetOf(room))
        every { serviceSession.room } returns room
        every { serviceSession.sessionType } returns SessionType.CODELABS

        // verify
        assertTrue { sut.isPass(serviceSession) }
    }

    @Test fun isPass_WhenCategoryFiltered() {
        // setup
        val category = Category(10, LocaledString("ツール", "Tool"))
        val sut = Filters(categories = mutableSetOf(category))
        every { speechSession.category } returns category

        // verify
        assertTrue { sut.isPass(speechSession) }
    }

    @Test fun isPass_WhenCategoryFilteredDifferentCategory() {
        // setup
        val category1 = Category(10, LocaledString("ツール1", "Tool1"))
        val category2 = Category(11, LocaledString("ツール2", "Tool2"))
        val sut = Filters(categories = mutableSetOf(category2))
        every { speechSession.category } returns category1

        // verify
        assertFalse { sut.isPass(speechSession) }
    }

    @Test fun isPass_WhenLangFiltered() {
        // setup
        val lang = Lang.JA
        val sut = Filters(langs = mutableSetOf(lang))
        every { speechSession.lang } returns Lang.JA

        // verify
        assertTrue { sut.isPass(speechSession) }
    }

    @Test fun isPass_WhenLangFilteredDifferentLang() {
        // setup
        val lang1 = Lang.JA
        val lang2 = Lang.EN
        val sut = Filters(langs = mutableSetOf(lang2))
        every { speechSession.lang } returns lang1

        // verify
        assertFalse { sut.isPass(speechSession) }
    }

    @Test fun isPass_WhenLangSupportFiltered() {
        // setup
        val langSupport = LangSupport.INTERPRETATION
        val sut = Filters(langSupports = mutableSetOf(langSupport))
        every { speechSession.isInterpretationTarget } returns true

        // verify
        assertTrue { sut.isPass(speechSession) }
    }

    @Test fun isPass_WhenLangSupportFilteredWithoutInterpretationTarget() {
        // setup
        val langSupport = LangSupport.INTERPRETATION
        val sut = Filters(langSupports = mutableSetOf(langSupport))
        every { speechSession.isInterpretationTarget } returns false

        // verify
        assertFalse { sut.isPass(speechSession) }
    }

    private fun speechSessionMock(isBeginner: Boolean = false): SpeechSession {
        val speechSessionBeginner = mockk<SpeechSession>()
        every { speechSessionBeginner.forBeginners } returns isBeginner
        return speechSessionBeginner
    }

    @Test fun audienceCategoryFilter_Empty() {
        // setup
        val filter = Filters(audienceCategories = mutableSetOf())

        // verify
        // pass all
        assertTrue { filter.isPass(speechSessionMock(isBeginner = true)) }
        assertTrue { filter.isPass(speechSessionMock(isBeginner = false)) }
    }

    @Test fun audienceCategoryFilter_Beginner() {
        // setup
        val filter = Filters(audienceCategories = mutableSetOf(AudienceCategory.BEGINNERS))

        // verify
        // pass only beginner
        assertTrue { filter.isPass(speechSessionMock(isBeginner = true)) }
        assertFalse { filter.isPass(speechSessionMock(isBeginner = false)) }
    }

    @Test fun audienceCategoryFilter_Unspecified() {
        // setup
        val filter = Filters(audienceCategories = mutableSetOf(AudienceCategory.UNSPECIFIED))

        // verify
        // pass only not beginner
        assertFalse { filter.isPass(speechSessionMock(isBeginner = true)) }
        assertTrue { filter.isPass(speechSessionMock(isBeginner = false)) }
    }

    @Test fun audienceCategoryFilter_Both() {
        // setup
        val filter = Filters(audienceCategories = mutableSetOf(
            AudienceCategory.BEGINNERS,
            AudienceCategory.UNSPECIFIED)
        )

        // verify
        // pass all
        assertTrue { filter.isPass(speechSessionMock(isBeginner = true)) }
        assertTrue { filter.isPass(speechSessionMock(isBeginner = false)) }
    }
}
