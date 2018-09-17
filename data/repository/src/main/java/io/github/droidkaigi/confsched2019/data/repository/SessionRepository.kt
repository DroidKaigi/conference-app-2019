package io.github.droidkaigi.confsched2019.data.repository

import io.github.droidkaigi.confsched2019.session.model.Session

interface SessionRepository {
    suspend fun sessions(): List<Session>

    suspend fun refresh()
    suspend fun toggleFavorite(session: Session.SpeechSession)
}
