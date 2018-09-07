package io.github.droidkaigi.confsched2019.session.ui.actioncreator

import confsched2018.droidkaigi.github.io.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.data.db.SessionDatabase
import io.github.droidkaigi.confsched2019.session.model.Action
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class AllSessionActionCreator @Inject constructor(
        val dispatcher: Dispatcher,
        val sessionDatabase: SessionDatabase
) {
    fun refresh() {
        launch {
            val sessions = sessionDatabase.sessions()
            dispatcher.send(Action.AllSessionLoaded(sessions))
        }
    }
}

