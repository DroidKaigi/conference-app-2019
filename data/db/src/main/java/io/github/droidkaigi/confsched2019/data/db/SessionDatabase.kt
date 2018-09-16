package io.github.droidkaigi.confsched2019.data.db

import io.github.droidkaigi.confsched2019.data.db.entity.SessionEntity
import io.github.droidkaigi.confsched2019.session.model.Session
import kotlinx.coroutines.experimental.channels.ReceiveChannel

interface SessionDatabase {
    fun sessionsChannel(): ReceiveChannel<List<SessionEntity>>
    suspend fun sessions(): List<SessionEntity>
    fun save(sessions: List<Session.SpeechSession>)
}
