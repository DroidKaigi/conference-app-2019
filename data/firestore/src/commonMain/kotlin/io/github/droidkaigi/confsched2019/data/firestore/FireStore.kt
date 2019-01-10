package io.github.droidkaigi.confsched2019.data.firestore

import io.github.droidkaigi.confsched2019.model.Announcement

interface Firestore {
    suspend fun getFavoriteSessionIds(): List<String>
    suspend fun toggleFavorite(sessionId: String)
    suspend fun getAnnouncements(): List<Announcement>
}
