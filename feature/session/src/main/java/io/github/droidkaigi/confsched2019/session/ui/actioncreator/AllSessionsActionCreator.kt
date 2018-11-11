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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@AllSessionsScope
@ExperimentalCoroutinesApi
class AllSessionsActionCreator @Inject constructor(
    val dispatcher: Dispatcher,
    val sessionRepository: SessionRepository,
    @AllSessionsScope val lifecycle: Lifecycle
) : CoroutineScope by lifecycle.coroutineScope {
    fun load(filters: Filters) = launch {
        try {
            dispatcher.send(Action.AllSessionLoadingStateChanged(LoadingState.LOADING))
            loadContent(filters)
        } catch (e: Exception) {
            // TODO: Error Handling
            throw e
        } finally {
            dispatcher.send(Action.AllSessionLoadingStateChanged(LoadingState.FINISHED))
        }
    }

    private suspend fun loadContent(filters: Filters) {
        val sessionContents = sessionRepository.sessionContents()
        val filteredSessionContents = sessionContents.copy(
            sessions = sessionContents.sessions.filter(filters::isPass)
        )
        dispatcher.send(Action.AllSessionLoaded(filteredSessionContents))
    }

    fun toggleFavoriteAndLoad(
        session: Session.SpeechSession,
        filters: Filters
    ) {
        launch {
            try {
                dispatcher.send(Action.AllSessionLoadingStateChanged(LoadingState.LOADING))
                sessionRepository.toggleFavorite(session)
                loadContent(filters)
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

    fun changeFilter(room: Room, checked: Boolean) {
        dispatcher.launchAndSend(Action.RoomFilterChanged(room, checked))
    }

    fun changeFilter(topic: Topic, checked: Boolean) {
        dispatcher.launchAndSend(Action.TopicFilterChanged(topic, checked))
    }

    fun changeFilter(lang: Lang, checked: Boolean) {
        dispatcher.launchAndSend(Action.LangFilterChanged(lang, checked))
    }

    fun clearFilters() {
        dispatcher.launchAndSend(Action.FilterCleared())
    }
}
