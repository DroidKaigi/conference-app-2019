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
import io.github.droidkaigi.confsched2019.model.SessionTab
import io.github.droidkaigi.confsched2019.model.Topic
import io.github.droidkaigi.confsched2019.session.di.AllSessionsScope
import io.github.droidkaigi.confsched2019.system.actioncreator.ErrorHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AllSessionsScope
class SessionPagesActionCreator @Inject constructor(
    override val dispatcher: Dispatcher,
    val sessionRepository: SessionRepository,
    @AllSessionsScope val lifecycle: Lifecycle
) : CoroutineScope by lifecycle.coroutineScope,
    ErrorHandler {
    fun load(filters: Filters) = launch {
        try {
            dispatcher.dispatch(Action.SessionLoadingStateChanged(LoadingState.LOADING))
            loadContent(filters)
        } catch (e: Exception) {
            onError(e = e)
        } finally {
            dispatcher.dispatch(Action.SessionLoadingStateChanged(LoadingState.FINISHED))
        }
    }

    fun toggleFavoriteAndLoad(
        session: Session.SpeechSession,
        filters: Filters
    ) {
        launch {
            try {
                dispatcher.dispatch(Action.SessionLoadingStateChanged(LoadingState.LOADING))
                sessionRepository.toggleFavorite(session)
                loadContent(filters)
            } catch (e: Exception) {
                onError(e = e)
            } finally {
                dispatcher.dispatch(Action.SessionLoadingStateChanged(LoadingState.FINISHED))
            }
        }
    }

    private suspend fun loadContent(filters: Filters) {
        val sessionContents = sessionRepository.sessionContents()
        val filteredSessionContents = sessionContents.copy(
            sessions = sessionContents.sessions.filter(filters::isPass)
        )
        dispatcher.dispatch(Action.SessionsLoaded(filteredSessionContents))
    }

    fun selectTab(sessionTab: SessionTab) {
        dispatcher.launchAndDispatch(Action.SessionTabSelected(sessionTab))
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
}
