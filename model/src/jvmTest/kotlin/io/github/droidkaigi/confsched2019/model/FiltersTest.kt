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
        private val param: Param<ServiceSession>
    ) {
        companion object {
            private val serviceSession: ServiceSession = mockk()

            @JvmStatic
            @Parameterized.Parameters(name = "{0}")
            fun testParams() = listOf<Param<ServiceSession>>(
                Param(
                    title = "AFTER_PARTY is not filterable and passed",
                    sessionSetup = {
                        every { sessionType } returns SessionType.AFTER_PARTY
                    },
                    expected = true
                ),
                Param(
                    title = "LUNCH is not filterable and passed",
                    sessionSetup = {
                        every { sessionType } returns SessionType.LUNCH
                    },
                    expected = true
                ),
                Param(
                    title = "room1 filter passes CODELABS in room1",
                    filters = Filters(rooms = setOf(room1)),
                    sessionSetup = {
                        every { sessionType } returns SessionType.CODELABS
                        every { room } returns room1
                    },
                    expected = true
                ),
                Param(
                    title = "room1 filter does not pass CODELABS in room2",
                    filters = Filters(rooms = setOf(room1)),
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

    @RunWith(Parameterized::class)
    class WhenSpeechSessionIsChecked(
        private val param: Param<SpeechSession>
    ) {
        companion object {
            private val speechSession: SpeechSession = mockk()

            @JvmStatic
            @Parameterized.Parameters(name = "{0}")
            fun params() = listOf<Param<SpeechSession>>(
                Param(
                    title = "empty filter passes empty session",
                    expected = true
                ),
                Param(
                    title = "room1 filter passes session in room1",
                    filters = Filters(rooms = setOf(room1)),
                    sessionSetup = {
                        every { room } returns room1
                    },
                    expected = true
                ),
                Param(
                    title = "room1 filter does not pass session in room2",
                    filters = Filters(rooms = setOf(room1)),
                    sessionSetup = {
                        every { room } returns room2
                    },
                    expected = false
                ),
                Param(
                    title = "category1 filter passes category1 session",
                    filters = Filters(categories = setOf(category1)),
                    sessionSetup = {
                        every { category } returns category1
                    },
                    expected = true
                ),
                Param(
                    title = "category2 filter does not pass category1 session",
                    filters = Filters(categories = setOf(category1)),
                    sessionSetup = {
                        every { category } returns category2
                    },
                    expected = false
                ),
                Param(
                    title = "JA filter passes Japanese session",
                    filters = Filters(langs = setOf(Lang.JA)),
                    sessionSetup = {
                        every { lang } returns Lang.JA
                    },
                    expected = true
                ),
                Param(
                    title = "JA filter does not pass English session",
                    filters = Filters(langs = setOf(Lang.JA)),
                    sessionSetup = {
                        every { lang } returns Lang.EN
                    },
                    expected = false
                ),
                Param(
                    title = "Interpretation filter passes interpretation session",
                    filters = Filters(langSupports = setOf(LangSupport.INTERPRETATION)),
                    sessionSetup = {
                        every { isInterpretationTarget } returns true
                    },
                    expected = true
                ),
                Param(
                    title = "Interpretation filter does not pass non interpretation session",
                    filters = Filters(langSupports = setOf(LangSupport.INTERPRETATION)),
                    sessionSetup = {
                        every { isInterpretationTarget } returns false
                    },
                    expected = false
                ),
                Param(
                    title = "empty filter passes beginners session",
                    sessionSetup = {
                        every { forBeginners } returns true
                    },
                    expected = true
                ),
                Param(
                    title = "empty filter passes non beginners session",
                    sessionSetup = {
                        every { forBeginners } returns false
                    },
                    expected = true
                ),
                Param(
                    title = "Beginners filter passes beginners session",
                    filters = Filters(audienceCategories = setOf(AudienceCategory.BEGINNERS)),
                    sessionSetup = {
                        every { forBeginners } returns true
                    },
                    expected = true
                ),
                Param(
                    title = "Beginners filter does not pass non beginners session",
                    filters = Filters(audienceCategories = setOf(AudienceCategory.BEGINNERS)),
                    sessionSetup = {
                        every { forBeginners } returns false
                    },
                    expected = false
                ),
                Param(
                    title = "Unspecified filter does not pass beginners session",
                    filters = Filters(audienceCategories = setOf(AudienceCategory.UNSPECIFIED)),
                    sessionSetup = {
                        every { forBeginners } returns true
                    },
                    expected = false
                ),
                Param(
                    title = "Unspecified filter passes non beginners session",
                    filters = Filters(audienceCategories = setOf(AudienceCategory.UNSPECIFIED)),
                    sessionSetup = {
                        every { forBeginners } returns false
                    },
                    expected = true
                ),
                Param(
                    title = "filter has Beginners and Unspecified passes beginners session",
                    filters = Filters(
                        audienceCategories = setOf(
                            AudienceCategory.BEGINNERS,
                            AudienceCategory.UNSPECIFIED
                        )
                    ),
                    sessionSetup = {
                        every { forBeginners } returns true
                    },
                    expected = true
                ),
                Param(
                    title = "filter has Beginners and Unspecified passes non beginners session",
                    filters = Filters(
                        audienceCategories = setOf(
                            AudienceCategory.BEGINNERS,
                            AudienceCategory.UNSPECIFIED
                        )
                    ),
                    sessionSetup = {
                        every { forBeginners } returns false
                    },
                    expected = true
                )
            )
        }

        @After fun tearDown() {
            clearMocks(speechSession)
        }

        @Test fun isPass_forSpeechSession() = with(param) {
            // setup
            sessionSetup(speechSession)

            // verify
            assertEquals(expected = expected, actual = filters.isPass(speechSession))
        }
    }
}

data class Param<T>(
    val title: String,
    val filters: Filters = Filters(),
    val sessionSetup: T.() -> Unit = {},
    val expected: Boolean
) {
    override fun toString(): String = title
}
