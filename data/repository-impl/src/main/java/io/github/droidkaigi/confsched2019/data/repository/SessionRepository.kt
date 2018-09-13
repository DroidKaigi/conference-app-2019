package io.github.droidkaigi.confsched2019.data.repository

import io.github.droidkaigi.confsched2019.data.db.SessionDatabase
import io.github.droidkaigi.confsched2019.session.model.Session
import javax.inject.Inject

class DataSessionRepository @Inject constructor(val sessionDatabase: SessionDatabase) : SessionRepository {
    //    override fun sessionsChannel(): ReceiveChannel<List<Session>>{
//
//    }
    override suspend fun sessions(): List<Session> {
        return sessionDatabase.sessions()
    }

    override fun save(sessions: List<Session.SpeechSession>) {
        sessionDatabase.save(sessions)
    }
}
