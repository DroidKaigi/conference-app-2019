package io.github.droidkaigi.confsched2019.session.ui.actioncreator

import io.github.droidkaigi.confsched2019.data.repository.SessionRepository
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.model.Action
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.Session
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class SessionActionCreator @Inject constructor(
    val dispatcher: Dispatcher,
    val sessionRepository: SessionRepository
) : CoroutineScope by GlobalScope {

    fun load() = launch(coroutineContext) {
        dispatcher.send(Action.AllSessionLoadingStateChanged(LoadingState.LOADING))
        try {
            // fetch api data
            sessionRepository.refresh()
            dispatcher.send(Action.AllSessionLoadingStateChanged(LoadingState.FINISHED))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun toggleFavorite(session: Session.SpeechSession) {
        launch {
            try {
                dispatcher.send(Action.AllSessionLoadingStateChanged(LoadingState.LOADING))
                sessionRepository.toggleFavorite(session)
                dispatcher.send(Action.AllSessionLoadingStateChanged(LoadingState.FINISHED))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
