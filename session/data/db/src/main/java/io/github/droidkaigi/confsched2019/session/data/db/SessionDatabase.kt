package io.github.droidkaigi.confsched2019.session.data.db

import io.github.droidkaigi.confsched2019.session.model.Session
import kotlinx.coroutines.experimental.channels.ReceiveChannel

interface SessionDatabase {
    fun getAllSessions(): ReceiveChannel<List<Session>>
    fun save(sessions: List<Session.SpeechSession>)
}