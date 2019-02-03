package io.github.droidkaigi.confsched2019.model

import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.After
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(Enclosed::class)
class FiltersTest {

    private companion object {
        val room1 = Room(10, "room1")
        val room2 = Room(11, "room2")
        val category1 = Category(10, LocaledString("ツール1", "Tool1"))
        val category2 = Category(11, LocaledString("ツール2", "Tool2"))
    }

    @RunWith(Parameterized::class)
    class WhenServiceSessionIsChecked(
        private val param: Param
    ) {
        data class Param(
            val title: String,
            val filters: Filters = Filters(),
            val sessionSetup: ServiceSession.() -> Unit,
            val expected: Boolean
        ) {
            override fun toString(): String = title
        }

        companion object {
            private val serviceSession: ServiceSession = mockk()

            @JvmStatic
            @Parameterized.Parameters(name = "{0}")
            fun testParams() = listOf(
                Param(
                    title = "AFTER_PARTY is not filterable",
                    sessionSetup = {
                        every { sessionType } returns SessionType.AFTER_PARTY
                    },
                    expected = true
                ),
                Param(
                    title = "LUNCH is not filterable",
                    sessionSetup = {
                        every { sessionType } returns SessionType.LUNCH
                    },
                    expected = true
                ),
                Param(
                    title = "CODELABS in room1 is filterable and filtered by room1 returns true",
                    filters = Filters(rooms = mutableSetOf(room1)),
                    sessionSetup = {
                        every { sessionType } returns SessionType.CODELABS
                        every { room } returns room1
                    },
                    expected = true
                ),
                Param(
                    title = "CODELABS in room2 is filterable and filtered by room2 returns false",
                    filters = Filters(rooms = mutableSetOf(room1)),
                    sessionSetup = {
                        every { sessionType } returns SessionType.CODELABS
                        every { room } returns room2
                    },
                    expected = false
                )
            )
        }

        @After fun tearDown() {
            clearMocks(serviceSession)
        }

        @Test fun isPassForServiceSession(): Unit = with(param) {
            // setup
            sessionSetup(serviceSession)

            // verify
            assertEquals(expected = expected, actual = filters.isPass(serviceSession))
        }
    }

    class WhenSpeechSessionIsChecked {
        companion object {
            private val speechSession: SpeechSession = mockk()
        }

        @After fun tearDown() {
            clearMocks(speechSession)
        }

        @Test fun isPass_WhenNotFiltered() {
            // setup
            val sut = Filters()

            // verify
            assertTrue { sut.isPass(speechSession) }
        }

        @Test fun isPass_WhenRoomFiltered() {
            // setup
            val sut = Filters(rooms = mutableSetOf(room1))
            every { speechSession.room } returns room1

            // verify
            assertTrue { sut.isPass(speechSession) }
        }

        @Test fun isPass_WhenRoomFilteredDifferentRoom() {
            // setup
            val sut = Filters(rooms = mutableSetOf(room2))
            every { speechSession.room } returns room1

            // verify
            assertFalse { sut.isPass(speechSession) }
        }

        @Test fun isPass_WhenCategoryFiltered() {
            // setup
            val sut = Filters(categories = mutableSetOf(category1))
            every { speechSession.category } returns category1

            // verify
            assertTrue { sut.isPass(speechSession) }
        }

        @Test fun isPass_WhenCategoryFilteredDifferentCategory() {
            // setup
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
}
