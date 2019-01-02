package io.github.droidkaigi.confsched2019.session.ui.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.shopify.livedataktx.map
import com.shopify.livedataktx.nonNull
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.toLiveData
import io.github.droidkaigi.confsched2019.model.Filters
import io.github.droidkaigi.confsched2019.model.Lang
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.Room
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.SessionContents
import io.github.droidkaigi.confsched2019.model.SessionPage
import io.github.droidkaigi.confsched2019.model.Topic
import kotlinx.coroutines.channels.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionsStore @Inject constructor(
    dispatcher: Dispatcher
) {
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

    private val sessionContents = dispatcher
        .subscribe<Action.SessionsLoaded>()
        .map { it.sessionContents }
        .toLiveData(SessionContents.EMPTY)
    val sessions: LiveData<List<Session>> = sessionContents
        .map { it?.sessions.orEmpty() }
    val langs: LiveData<List<Lang>> = sessionContents
        .map { it?.langs.orEmpty() }
    val topics: LiveData<List<Topic>> = sessionContents
        .map { it?.topics.orEmpty() }
    val rooms: LiveData<List<Room>> = sessionContents
        .map { it?.rooms.orEmpty() }

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
        addSource(roomFilterChanged) { roomFilterChanged ->
            roomFilterChanged?.let {
                if (roomFilterChanged.checked) {
                    filters.rooms.add(it.room)
                } else {
                    filters.rooms.remove(it.room)
                }
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
            }
            value = langFilterChanged
        }
        addSource(filterCleared) {
            filters.clear()
            value = it
        }
    }
    val filters = Filters()

    val selectedTab: LiveData<SessionPage> = dispatcher
        .subscribe<Action.SessionPageSelected>()
        .map { it.sessionPage }
        .toLiveData(SessionPage.pages[0])

    fun daySessions(day: Int): LiveData<List<Session>> {
        return sessions
            .map { it.orEmpty().filter { it.dayNumber == day } }
    }

    fun speakerSession(sessionId: String): LiveData<Session.SpeechSession> {
        return sessions
            .map { it.orEmpty().firstOrNull() { it.id == sessionId } as? Session.SpeechSession }
            .nonNull()
    }

    fun speakerSessionBySpeakerId(speakerId: String): LiveData<Session.SpeechSession?> {
        return sessions
            .map { sessions ->
                sessions?.firstOrNull() { session ->
                    val speechSession: Session.SpeechSession =
                        session as? Session.SpeechSession ?: return@firstOrNull false
                    speechSession.speakers.find { it.id == speakerId } != null
                } as? Session.SpeechSession
            }
    }

    fun favoriteSessions(): LiveData<List<Session.SpeechSession>> {
        return sessions
            .map {
                it.orEmpty().filterIsInstance<Session.SpeechSession>()
                    .filter { it.isFavorited }
            }
    }
}
