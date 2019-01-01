package io.github.droidkaigi.confsched2019.session.ui.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.shopify.livedataktx.combineWith
import com.shopify.livedataktx.map
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.mutableLiveDataOf
import io.github.droidkaigi.confsched2019.ext.android.notifyChange
import io.github.droidkaigi.confsched2019.ext.android.requireValue
import io.github.droidkaigi.confsched2019.ext.android.toLiveData
import io.github.droidkaigi.confsched2019.model.Filters
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.SessionPage
import kotlinx.coroutines.channels.map
import javax.inject.Inject

class SessionPagesStore @Inject constructor(
    dispatcher: Dispatcher
) : ViewModel() {
    val loadingState = dispatcher
        .subscribe<Action.SessionLoadingStateChanged>()
        .map { it.loadingState }
        .toLiveData(LoadingState.INITIALIZED)
    val isInitialized: Boolean
        get() = loadingState.value == LoadingState.INITIALIZED
    val isLoading
        get() = loadingState.value == LoadingState.LOADING
    val isLoaded: Boolean
        get() = loadingState.value == LoadingState.LOADED

    private val sessions: LiveData<List<Session>> = dispatcher
        .subscribe<Action.SessionsLoaded>()
        .map { it.sessions }
        .toLiveData(listOf())

    private val filtersLiveData = mutableLiveDataOf(Filters())
    val filters: Filters get() = filtersLiveData.requireValue()

    val filteredSessions: LiveData<List<Session>> = sessions
        .combineWith(filtersLiveData) { sessions, filters ->
            sessions ?: return@combineWith listOf<Session>()
            sessions.filter { session ->
                filters?.isPass(session) ?: true
            }
        }
        .map { it.orEmpty() }

    private val roomFilterChanged = dispatcher
        .subscribe<Action.RoomFilterChanged>()
        .toLiveData()

    private val topicFilterChanged = dispatcher
        .subscribe<Action.TopicFilterChanged>()
        .toLiveData()

    private val langFilterChanged = dispatcher
        .subscribe<Action.LangFilterChanged>()
        .toLiveData()

    private val filterCleared = dispatcher
        .subscribe<Action.FilterCleared>()
        .toLiveData()

    val filtersChange: LiveData<Any> = MediatorLiveData<Any>().apply {
        val filters = filtersLiveData.requireValue()
        addSource(roomFilterChanged) { roomFilterChanged ->
            roomFilterChanged?.let {
                if (roomFilterChanged.checked) {
                    filters.rooms.add(it.room)
                } else {
                    filters.rooms.remove(it.room)
                }
                filtersLiveData.notifyChange()
            }
            value = roomFilterChanged
        }
        addSource(topicFilterChanged) { topicFilterChanged ->
            topicFilterChanged?.let {
                if (topicFilterChanged.checked) {
                    filters.topics.add(it.topic)
                } else {
                    filters.topics.remove(it.topic)
                }
                filtersLiveData.notifyChange()
            }
            value = topicFilterChanged
        }
        addSource(langFilterChanged) { langFilterChanged ->
            langFilterChanged?.let {
                if (langFilterChanged.checked) {
                    filters.langs.add(it.lang)
                } else {
                    filters.langs.remove(it.lang)
                }
                filtersLiveData.notifyChange()
            }
            value = langFilterChanged
        }
        addSource(filterCleared) {
            filters.clear()
            filtersLiveData.notifyChange()
            value = it
        }
    }

    val selectedTab: LiveData<SessionPage> = dispatcher
        .subscribe<Action.SessionPageSelected>()
        .map { it.sessionPage }
        .toLiveData(SessionPage.pages[0])

    fun filteredDaySessions(day: Int): LiveData<List<Session>> {
        return filteredSessions
            .map { sessions ->
                sessions.orEmpty().filter { session -> session.dayNumber == day }
            }
    }

    fun filteredFavoriteSessions(): LiveData<List<Session.SpeechSession>> {
        return filteredSessions
            .map { sessions ->
                sessions.orEmpty().filterIsInstance<Session.SpeechSession>()
                    .filter { session -> session.isFavorited }
            }
    }
}
