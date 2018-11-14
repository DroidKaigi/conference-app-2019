package io.github.droidkaigi.confsched2019.session.ui.actioncreator

import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.data.repository.SessionRepository
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.system.actioncreator.ErrorHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class SessionsActionCreator @Inject constructor(
    override val dispatcher: Dispatcher,
    private val sessionRepository: SessionRepository
) : ErrorHandler {

    fun load() = GlobalScope.launch {
        dispatcher.dispatch(Action.SessionLoadingStateChanged(LoadingState.LOADING))
        try {
            // load db data
            val sessionContents = sessionRepository.sessionContents()
            dispatcher.dispatch(Action.AllSessionLoaded(sessionContents))

            // fetch api data
            sessionRepository.refresh()

            // reload db data
            val sessionContentsRefreshed = sessionRepository.sessionContents()
            dispatcher.dispatch(Action.AllSessionLoaded(sessionContentsRefreshed))

            dispatcher.dispatch(Action.SessionLoadingStateChanged(LoadingState.FINISHED))
        } catch (e: Exception) {
            onError(e = e)
        }
    }
}
