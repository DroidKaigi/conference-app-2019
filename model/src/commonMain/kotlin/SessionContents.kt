package io.github.droidkaigi.confsched2019.model

data class SessionContents(
    val sessions: List<Session>,
    val rooms: List<Room>,
    val langs: List<Lang>,
    val topics: List<Topic>
) {
    companion object {
        val EMPTY = SessionContents(listOf(), listOf(), listOf(), listOf())
    }
}

