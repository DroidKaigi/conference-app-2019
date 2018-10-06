package io.github.droidkaigi.confsched2019.data.repository

import io.github.droidkaigi.confsched2019.model.Session

interface SessionRepository {
    suspend fun sessions(withFavorite: Boolean): List<Session>

    suspend fun refresh()
    suspend fun toggleFavorite(session: Session.SpeechSession)
}
