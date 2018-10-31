package io.github.droidkaigi.confsched2019.model

sealed class SessionTab {

    object Favorite : SessionTab() {
        override val title = "Favorite"
    }

    open class Day(
        override val title: String,
        val day: Int
    ) : SessionTab() {
    }

    object Day1 : Day("Day1", 1)
    object Day2 : Day("Day2", 2)

    abstract val title: String

    companion object {
        val tabs = listOf(Day1, Day2, Favorite)
    }
}
