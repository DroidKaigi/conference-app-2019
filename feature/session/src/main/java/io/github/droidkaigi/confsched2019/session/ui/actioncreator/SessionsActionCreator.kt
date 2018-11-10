package io.github.droidkaigi.confsched2019.session.ui.actioncreator

import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.data.repository.SessionRepository
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.model.LoadingState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

class SessionsActionCreator @Inject constructor(
    val dispatcher: Dispatcher,
    private val sessionRepository: SessionRepository
) : CoroutineScope by GlobalScope + SupervisorJob() {

    fun load() = launch {
        dispatcher.send(Action.AllSessionLoadingStateChanged(LoadingState.LOADING))
        try {
            // fetch api data
            sessionRepository.refresh()
            dispatcher.send(Action.AllSessionLoadingStateChanged(LoadingState.FINISHED))
        } catch (e: Exception) {
            // TODO: error handling
            throw e
        }
    }
}
