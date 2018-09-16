package io.github.droidkaigi.confsched2019.data.repository

import io.github.droidkaigi.confsched2019.data.db.SessionDatabase
import io.github.droidkaigi.confsched2019.data.db.entity.SessionEntity
import io.github.droidkaigi.confsched2019.data.firestore.FireStore
import io.github.droidkaigi.confsched2019.session.model.Session
import javax.inject.Inject

class DataSessionRepository @Inject constructor(
        val sessionDatabase: SessionDatabase,
        val fireStore: FireStore
) : SessionRepository {
    override suspend fun sessions(): List<Session> {
        return sessionDatabase.sessions().map {
            it.mapSession()
        }
    }

    private fun SessionEntity.mapSession(): Session.SpeechSession {
        return Session.SpeechSession(
                id,
                org.threeten.bp.Instant.ofEpochMilli(stime),
                org.threeten.bp.Instant.ofEpochMilli(etime),
                title,
                desc
        )
    }


    override fun save(sessions: List<Session.SpeechSession>) {
        sessionDatabase.save(sessions)
    }
}
