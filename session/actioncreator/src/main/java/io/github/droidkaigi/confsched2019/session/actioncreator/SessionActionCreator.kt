package io.github.droidkaigi.confsched2019.session.actioncreator

import io.github.droidkaigi.confsched2019.session.data.api.getSessions
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch

class SessionActionCreator {
    suspend fun load() = launch(CommonPool) {
        try {
            val sessions = getSessions()
            println(sessions)
            // TODO: save to db
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}

