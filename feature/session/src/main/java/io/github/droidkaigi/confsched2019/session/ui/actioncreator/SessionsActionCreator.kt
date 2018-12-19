package io.github.droidkaigi.confsched2019.session.ui.actioncreator

import androidx.lifecycle.Lifecycle
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.data.repository.SessionRepository
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.coroutineScope
import io.github.droidkaigi.confsched2019.model.Filters
import io.github.droidkaigi.confsched2019.model.Lang
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.Room
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.SessionContents
import io.github.droidkaigi.confsched2019.model.SessionPage
import io.github.droidkaigi.confsched2019.model.Topic
import io.github.droidkaigi.confsched2019.session.di.SessionPagesScope
import io.github.droidkaigi.confsched2019.system.actioncreator.ErrorHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@SessionPagesScope
class SessionsActionCreator @Inject constructor(
    override val dispatcher: Dispatcher,
    private val sessionRepository: SessionRepository,
    @SessionPagesScope val lifecycle: Lifecycle
) : CoroutineScope by lifecycle.coroutineScope,
    ErrorHandler {
    fun refresh() = launch {
        try {
            dispatcher.dispatch(Action.SessionLoadingStateChanged(LoadingState.LOADING))
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
            dispatcher.dispatch(Action.SessionLoadingStateChanged(LoadingState.LOADED))
        }
    }

    fun load(filters: Filters) {
        launch {
            try {
                dispatcher.dispatchLoadingState(LoadingState.LOADING)
                val sessionContents = loadContent(filters)
                dispatcher.dispatch(Action.SessionsLoaded(sessionContents))
            } catch (e: Exception) {
                onError(e)
            } finally {
                dispatcher.dispatchLoadingState(LoadingState.LOADED)
            }
        }
    }

    fun toggleFavoriteAndLoad(
        session: Session.SpeechSession,
        filters: Filters
    ) {
        launch {
            try {
                dispatcher.dispatchLoadingState(LoadingState.LOADING)
                sessionRepository.toggleFavorite(session)
                val sessionContents = loadContent(filters)
                dispatcher.dispatch(Action.SessionsLoaded(sessionContents))
            } catch (e: Exception) {
                onError(e)
            } finally {
                dispatcher.dispatchLoadingState(LoadingState.LOADED)
            }
        }
    }

    private suspend fun loadContent(filters: Filters): SessionContents {
        val sessionContents = sessionRepository.sessionContents()
        return sessionContents.copy(
            sessions = sessionContents.sessions.filter(filters::isPass)
        )
    }

    fun selectTab(sessionPage: SessionPage) {
        dispatcher.launchAndDispatch(Action.SessionPageSelected(sessionPage))
    }

    fun changeFilter(room: Room, checked: Boolean) {
        dispatcher.launchAndDispatch(Action.RoomFilterChanged(room, checked))
    }

    fun changeFilter(topic: Topic, checked: Boolean) {
        dispatcher.launchAndDispatch(Action.TopicFilterChanged(topic, checked))
    }

    fun changeFilter(lang: Lang, checked: Boolean) {
        dispatcher.launchAndDispatch(Action.LangFilterChanged(lang, checked))
    }

    fun clearFilters() {
        dispatcher.launchAndDispatch(Action.FilterCleared())
    }

    private suspend fun Dispatcher.dispatchLoadingState(loadingState: LoadingState) {
        dispatch(Action.SessionLoadingStateChanged(loadingState))
    }
}
