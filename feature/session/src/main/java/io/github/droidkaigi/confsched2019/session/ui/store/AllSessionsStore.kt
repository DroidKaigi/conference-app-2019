package io.github.droidkaigi.confsched2019.session.ui.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.shopify.livedataktx.combineWith
import com.shopify.livedataktx.distinct
import com.shopify.livedataktx.map
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.toLiveData
import io.github.droidkaigi.confsched2019.model.Filters
import io.github.droidkaigi.confsched2019.model.Lang
import io.github.droidkaigi.confsched2019.model.Room
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.SessionContents
import io.github.droidkaigi.confsched2019.model.SessionTab
import io.github.droidkaigi.confsched2019.model.Topic
import kotlinx.coroutines.channels.map
import javax.inject.Inject

class AllSessionsStore @Inject constructor(
    dispatcher: Dispatcher
) : ViewModel() {
    private val contents = dispatcher
        .subscribe<Action.AllSessionLoaded>()
        .map { it.sessionContents }
        .toLiveData(SessionContents.EMPTY)
    val sessions: LiveData<List<Session>> = contents
        .map { it?.sessions.orEmpty() }
    val langs: LiveData<List<Lang>> = contents
        .map { it?.langs.orEmpty() }
    val topics: LiveData<List<Topic>> = contents
        .map { it?.topics.orEmpty() }
    val rooms: LiveData<List<Room>> = contents
        .map { it?.rooms.orEmpty() }

    private val roomFilterChanged = dispatcher
        .subscribe<Action.RoomFilterChanged>()
        .toLiveData()
        .distinct()
        .map { roomFilterChanged ->
            roomFilterChanged?.let {
                if (roomFilterChanged.checked) {
                    filters.rooms.add(it.room)
                } else {
                    filters.rooms.remove(it.room)
                }
            }
            roomFilterChanged
        }
    private val topicFilterChanged = dispatcher
        .subscribe<Action.TopicFilterChanged>()
        .toLiveData()
        .distinct()
        .map { topicFilterChanged ->
            topicFilterChanged?.let {
                if (topicFilterChanged.checked) {
                    filters.topics.add(it.topic)
                } else {
                    filters.topics.remove(it.topic)
                }
            }
            roomFilterChanged
        }
    private val langFilterChanged = dispatcher
        .subscribe<Action.LangFilterChanged>()
        .toLiveData()
        .distinct()
        .map { langFilterChanged ->
            langFilterChanged?.let {
                if (langFilterChanged.checked) {
                    filters.langs.add(it.lang)
                } else {
                    filters.langs.remove(it.lang)
                }
            }
            roomFilterChanged
        }
    private val filterCleared = dispatcher
        .subscribe<Action.FilterCleared>()
        .toLiveData()
        .distinct()
        .map {
            filters.clear()
            Unit
        }
    val filtersChange: LiveData<Unit> = roomFilterChanged
        .combineWith(topicFilterChanged) { _, topic -> }
        .combineWith(langFilterChanged) { unit, liveData -> }
        .combineWith(filterCleared) { unit1, unit2 -> }
    val filters = Filters()

    val selectedTab: LiveData<SessionTab> = dispatcher
        .subscribe<Action.SessionTabSelected>()
        .map { it.sessionTab }
        .toLiveData(SessionTab.tabs[0])

    fun daySessions(day: Int): LiveData<List<Session>> {
        return sessions
            .map { it.orEmpty().filter { it.dayNumber == day } }
    }

    fun favoriteSessions(): LiveData<List<Session.SpeechSession>> {
        return sessions
            .map {
                it.orEmpty().filterIsInstance<Session.SpeechSession>()
                    .filter { it.isFavorited }
            }
    }
}
