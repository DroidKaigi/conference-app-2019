package io.github.droidkaigi.confsched2019.data.db

import io.github.droidkaigi.confsched2019.session.model.Session
import kotlinx.coroutines.experimental.channels.ReceiveChannel

interface SessionDatabase {
    fun sessionsChannel(): ReceiveChannel<List<Session>>
    suspend fun sessions(): List<Session>
    fun save(sessions: List<Session.SpeechSession>)
}
