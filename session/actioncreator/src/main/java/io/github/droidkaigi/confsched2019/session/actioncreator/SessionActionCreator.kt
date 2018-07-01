package io.github.droidkaigi.confsched2019.session.actioncreator

import io.github.droidkaigi.confsched2019.session.data.getSessions
import kotlinx.coroutines.experimental.launch

class SessionActionCreator {
    fun reload() {
        launch {
            try {
                val sessions = getSessions()
                println(sessions)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

