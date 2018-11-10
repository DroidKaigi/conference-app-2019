package io.github.droidkaigi.confsched2019.data.repository

import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.SessionContents
import kotlinx.coroutines.channels.ReceiveChannel

interface SessionRepository {
    suspend fun sessionContents(): SessionContents
    suspend fun sessionChannel(): ReceiveChannel<List<Session>>
    suspend fun refresh()
    suspend fun toggleFavorite(session: Session.SpeechSession)
}
