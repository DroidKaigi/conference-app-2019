package io.github.droidkaigi.confsched2019.data.firestore

import kotlinx.coroutines.channels.ReceiveChannel

interface FireStore {
    suspend fun getFavoriteSessionChannel(): ReceiveChannel<List<Int>>
    suspend fun getFavoriteSessionIds(): List<Int>
    suspend fun toggleFavorite(sessionId: String)
}
