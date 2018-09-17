package io.github.droidkaigi.confsched2019.data.firestore

interface FireStore {
    fun getFavoriteSessionIds(): List<Int>
    fun toggleFavorite(id: String)
}
