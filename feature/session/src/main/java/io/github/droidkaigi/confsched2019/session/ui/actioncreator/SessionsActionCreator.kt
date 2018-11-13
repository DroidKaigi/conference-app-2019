package io.github.droidkaigi.confsched2019.session.ui.actioncreator

import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.data.repository.SessionRepository
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.model.LoadingState
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class SessionsActionCreator @Inject constructor(
    val dispatcher: Dispatcher,
    private val sessionRepository: SessionRepository
) {

    fun load() = GlobalScope.launch {
        println("SessionsActionCreator 1")
        dispatcher.send(Action.SessionLoadingStateChanged(LoadingState.LOADING))
        println("SessionsActionCreator 2")
        try {
            // load db data
            val sessionContents = sessionRepository.sessionContents()
            dispatcher.send(Action.AllSessionLoaded(sessionContents))
            println("SessionsActionCreator 3")

            // fetch api data
            sessionRepository.refresh()

            // reload db data
            val sessionContentsRefreshed = sessionRepository.sessionContents()
            dispatcher.send(Action.AllSessionLoaded(sessionContentsRefreshed))

            dispatcher.send(Action.SessionLoadingStateChanged(LoadingState.FINISHED))
        } catch (e: Exception) {
            // TODO: error handling
            throw e
        }
    }
}
