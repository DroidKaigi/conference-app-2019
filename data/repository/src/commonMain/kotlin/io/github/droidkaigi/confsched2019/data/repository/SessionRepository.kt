package io.github.droidkaigi.confsched2019.data.repository

import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.SessionContents
import io.github.droidkaigi.confsched2019.model.SessionFeedback
import io.github.droidkaigi.confsched2019.model.SpeechSession

interface SessionRepository {
    suspend fun sessionContents(): SessionContents
    suspend fun refresh()
    suspend fun toggleFavorite(session: Session)
    suspend fun sessionFeedback(sessionId: String): SessionFeedback
    suspend fun saveSessionFeedback(sessionFeedback: SessionFeedback)
    suspend fun submitSessionFeedback(
        session: SpeechSession,
        sessionFeedback: SessionFeedback
    )
}
