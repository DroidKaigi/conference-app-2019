package io.github.droidkaigi.confsched2019.data.firestore

interface FireStore {
    suspend fun getFavoriteSessionIds(): List<Int>
    suspend fun toggleFavorite(sessionId: String)
}
