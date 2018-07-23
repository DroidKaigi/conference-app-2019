package io.github.droidkaigi.confsched2019.session.model

import confsched2018.droidkaigi.github.io.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.session.data.api.SessionApi
import io.github.droidkaigi.confsched2019.session.data.db.SessionDatabase
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class SessionActionCreator @Inject constructor(
        val dispatcher: Dispatcher,
        val sessionDatabase: SessionDatabase,
        val sessionApi:SessionApi
) {

    suspend fun load() = launch(CommonPool) {
        try {
            val sessions = sessionApi.getSessions()
            sessionDatabase.save(sessions)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

