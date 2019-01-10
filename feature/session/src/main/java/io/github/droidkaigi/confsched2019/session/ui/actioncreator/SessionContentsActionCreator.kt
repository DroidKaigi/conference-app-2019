package io.github.droidkaigi.confsched2019.session.ui.actioncreator

import androidx.lifecycle.Lifecycle
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.data.repository.SessionRepository
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.coroutineScope
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.system.actioncreator.ErrorHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import timber.log.debug
import javax.inject.Inject

@PageScope
class SessionContentsActionCreator @Inject constructor(
    override val dispatcher: Dispatcher,
    private val sessionRepository: SessionRepository,
    @PageScope private val lifecycle: Lifecycle
) : CoroutineScope by lifecycle.coroutineScope,
    ErrorHandler {
    fun refresh() = launch {
        try {
            Timber.debug { "SessionContentsActionCreator: refresh start" }
            dispatcher.dispatchLoadingState(LoadingState.LOADING)
            Timber.debug { "SessionContentsActionCreator: At first, load db data" }
            // At first, load db data
            val sessionContents = sessionRepository.sessionContents()
            dispatcher.dispatch(Action.SessionContentsLoaded(sessionContents))

            // fetch api data
            Timber.debug { "SessionContentsActionCreator: fetch api data" }
            sessionRepository.refresh()

            // reload db data
            Timber.debug { "SessionContentsActionCreator: reload db data" }
            val refreshedSessionContents = sessionRepository.sessionContents()
            dispatcher.dispatch(Action.SessionContentsLoaded(refreshedSessionContents))
            Timber.debug { "SessionContentsActionCreator: refresh end" }
            dispatcher.dispatchLoadingState(LoadingState.LOADED)
        } catch (e: Exception) {
            onError(e)
            dispatcher.dispatchLoadingState(LoadingState.INITIALIZED)
        }
    }

    fun load() {
        launch {
            try {
                dispatcher.dispatchLoadingState(LoadingState.LOADING)
                val sessionContents = sessionRepository.sessionContents()
                dispatcher.dispatch(Action.SessionContentsLoaded(sessionContents))
                dispatcher.dispatchLoadingState(LoadingState.LOADED)
            } catch (e: Exception) {
                onError(e)
                dispatcher.dispatchLoadingState(LoadingState.INITIALIZED)
            }
        }
    }

    fun toggleFavorite(session: Session) {
        launch {
            try {
                dispatcher.dispatchLoadingState(LoadingState.LOADING)
                sessionRepository.toggleFavorite(session)
                val sessionContents = sessionRepository.sessionContents()
                dispatcher.dispatch(Action.SessionContentsLoaded(sessionContents))
                dispatcher.dispatchLoadingState(LoadingState.LOADED)
            } catch (e: Exception) {
                onError(e)
                dispatcher.dispatchLoadingState(LoadingState.INITIALIZED)
            }
        }
    }

    private suspend fun Dispatcher.dispatchLoadingState(loadingState: LoadingState) {
        dispatch(Action.SessionLoadingStateChanged(loadingState))
    }
}
