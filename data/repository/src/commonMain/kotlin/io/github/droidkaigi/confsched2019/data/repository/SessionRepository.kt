package io.github.droidkaigi.confsched2019.data.repository

import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.SessionContents
import io.github.droidkaigi.confsched2019.model.SessionFeedback

interface SessionRepository {
    suspend fun sessionContents(): SessionContents
    suspend fun refresh()
    suspend fun toggleFavorite(session: Session)
    suspend fun submitSessionFeedback(
        session: Session.SpeechSession,
        sessionFeedback: SessionFeedback
    )
}
