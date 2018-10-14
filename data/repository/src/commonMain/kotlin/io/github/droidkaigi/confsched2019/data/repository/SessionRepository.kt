package io.github.droidkaigi.confsched2019.data.repository

import io.github.droidkaigi.confsched2019.model.Session
import kotlinx.coroutines.channels.ReceiveChannel

interface SessionRepository {
    suspend fun sessions(withFavorite: Boolean): List<Session>
    suspend fun sessionChannel(): ReceiveChannel<List<Session>>

    suspend fun refresh()
    suspend fun toggleFavorite(session: Session.SpeechSession)
}
