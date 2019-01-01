package io.github.droidkaigi.confsched2019.model

class SearchResult(
    val sessions: List<Session>,
    val speakers: List<Speaker>
) {
    companion object {
        val EMPTY = SearchResult(listOf(), listOf())
    }
}
