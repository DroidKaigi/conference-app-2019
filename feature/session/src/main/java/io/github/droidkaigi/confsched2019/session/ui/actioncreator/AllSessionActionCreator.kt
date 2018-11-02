package io.github.droidkaigi.confsched2019.session.ui.actioncreator

import androidx.lifecycle.Lifecycle
import io.github.droidkaigi.confsched2019.data.repository.SessionRepository
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.coroutineScope
import io.github.droidkaigi.confsched2019.model.Action
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.SessionTab
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class AllSessionActionCreator @Inject constructor(
    val dispatcher: Dispatcher,
    val sessionRepository: SessionRepository,
    @Named("AllSessionsFragment") val lifecycle: Lifecycle
) : CoroutineScope by lifecycle.coroutineScope {
    fun load() = launch {
        try {
            dispatcher.send(Action.AllSessionLoadingStateChanged(LoadingState.LOADING))
            val sessions = sessionRepository.sessions()
            dispatcher.send(Action.AllSessionLoaded(sessions))
        } catch (e: Exception) {
            // TODO: Error Handling
            throw e
        } finally {
            dispatcher.send(Action.AllSessionLoadingStateChanged(LoadingState.FINISHED))
        }
    }

    fun toggleFavorite(session: Session.SpeechSession) {
        launch {
            try {
                dispatcher.send(Action.AllSessionLoadingStateChanged(LoadingState.LOADING))
                sessionRepository.toggleFavorite(session)
                val sessions = sessionRepository.sessions()
                dispatcher.send(Action.AllSessionLoaded(sessions))
            } catch (e: Exception) {
                // TODO: error handling
                throw e
            } finally {
                dispatcher.send(Action.AllSessionLoadingStateChanged(LoadingState.FINISHED))
            }
        }
    }

    fun selectTab(sessionTab: SessionTab) {
        dispatcher.launchAndSend(Action.SessionTabSelected(sessionTab))
    }
}
