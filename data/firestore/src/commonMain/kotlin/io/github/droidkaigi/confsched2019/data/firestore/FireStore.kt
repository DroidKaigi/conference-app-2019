package io.github.droidkaigi.confsched2019.data.firestore

import kotlinx.coroutines.channels.Channel

interface FireStore {
    suspend fun getFavoriteSessionChannel(): Channel<List<Int>>
    suspend fun getFavoriteSessionIds(): List<Int>
    suspend fun toggleFavorite(sessionId: String)
}
