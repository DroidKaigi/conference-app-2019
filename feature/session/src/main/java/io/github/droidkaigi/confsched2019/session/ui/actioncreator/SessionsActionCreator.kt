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

    fun refresh() = GlobalScope.launch {
        dispatcher.dispatch(Action.SessionRefreshStateChanged(LoadingState.LOADING))
        try {
            // refresh db data
            val sessionContents = sessionRepository.sessionContents()
            dispatcher.dispatch(Action.SessionsLoaded(sessionContents))

            // fetch api data
            sessionRepository.refresh()

            // reload db data
            val sessionContentsRefreshed = sessionRepository.sessionContents()
            dispatcher.dispatch(Action.SessionsLoaded(sessionContentsRefreshed))
        } catch (e: Exception) {
            onError(e)
        } finally {
            dispatcher.dispatch(Action.SessionRefreshStateChanged(LoadingState.FINISHED))
            dispatcher.dispatch(Action.SessionLoadingStateChanged(LoadingState.FINISHED))
        }
    }
}
