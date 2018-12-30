package io.github.droidkaigi.confsched2019.data.repository

import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.SessionContents

interface SessionRepository {
    suspend fun sessionContents(): SessionContents
    suspend fun refresh()
    suspend fun toggleFavorite(session: Session.SpeechSession)
}
