package io.github.droidkaigi.confsched2019.data.firestore

interface Firestore {
    suspend fun getFavoriteSessionIds(): List<String>
    suspend fun toggleFavorite(sessionId: String)
}
