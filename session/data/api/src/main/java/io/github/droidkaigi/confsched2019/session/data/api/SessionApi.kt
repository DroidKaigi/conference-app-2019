package io.github.droidkaigi.confsched2019.session.data.api

import io.github.droidkaigi.confsched2019.session.model.Session

interface SessionApi {
    suspend fun getSessions(): List<Session.SpeechSession>
}
