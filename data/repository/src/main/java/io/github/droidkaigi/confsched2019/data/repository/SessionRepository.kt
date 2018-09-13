package io.github.droidkaigi.confsched2019.data.repository

import io.github.droidkaigi.confsched2019.session.model.Session

interface SessionRepository {
    //    fun sessionsChannel(): ReceiveChannel<List<Session>>
    suspend fun sessions(): List<Session>

    fun save(sessions: List<Session.SpeechSession>)
}
