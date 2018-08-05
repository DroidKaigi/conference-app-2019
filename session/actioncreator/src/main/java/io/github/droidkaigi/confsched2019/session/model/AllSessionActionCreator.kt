package io.github.droidkaigi.confsched2019.session.model

import confsched2018.droidkaigi.github.io.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.session.data.db.SessionDatabase
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject
import kotlin.coroutines.experimental.CoroutineContext

class AllSessionActionCreator @Inject constructor(
        val dispatcher: Dispatcher,
        val sessionDatabase: SessionDatabase
) {
    fun listen(context: CoroutineContext) {
        launch(context) {
            for (sessions in sessionDatabase.getAllSessions()) {
                dispatcher.send(Action.AllSessionLoaded(sessions))
            }
        }
    }
}

