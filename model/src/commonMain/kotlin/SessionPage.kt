package io.github.droidkaigi.confsched2019.model

sealed class SessionPage {

    object Favorite : SessionPage() {
        override val title = "MyPlan"
    }

    open class Day(
        override val title: String,
        val day: Int
    ) : SessionPage() {
    }

    object Day1 : Day("Day1", 1)
    object Day2 : Day("Day2", 2)

    abstract val title: String

    companion object {
        val pages = listOf(Day1, Day2, Favorite)

        fun pageOfDay(dayNumber: Int): Day {
            return pages.filterIsInstance<Day>().first { it.day == dayNumber }
        }
    }
}
