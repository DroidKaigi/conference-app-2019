package io.github.droidkaigi.confsched2019.session.ui.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.shopify.livedataktx.combineWith
import com.shopify.livedataktx.map
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.requireValue
import io.github.droidkaigi.confsched2019.ext.android.toLiveData
import io.github.droidkaigi.confsched2019.model.Filters
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.SessionPage
import kotlinx.coroutines.channels.map
import javax.inject.Inject

class SessionPagesStore @Inject constructor(
    dispatcher: Dispatcher
) : ViewModel() {
    private val sessions: LiveData<List<Session>> = dispatcher
        .subscribe<Action.SessionsLoaded>()
        .map { it.sessions }
        .toLiveData(listOf())

    private val roomFilterChanged = dispatcher
        .subscribe<Action.RoomFilterChanged>()
        .toLiveData()

    private val categoryFilterChanged = dispatcher
        .subscribe<Action.CategoryFilterChanged>()
        .toLiveData()

    private val langFilterChanged = dispatcher
        .subscribe<Action.LangFilterChanged>()
        .toLiveData()

    private val filterCleared = dispatcher
        .subscribe<Action.FilterCleared>()
        .toLiveData()

    val filters = MediatorLiveData<Filters>().apply {
        value = Filters()
        addSource(roomFilterChanged) { roomFilterChanged ->
            roomFilterChanged?.let {
                value = if (roomFilterChanged.checked) {
                    filtersValue.copy(rooms = filtersValue.rooms + it.room)
                } else {
                    filtersValue.copy(rooms = filtersValue.rooms - it.room)
                }
            }
        }
        addSource(categoryFilterChanged) { categoryFilterChanged ->
            categoryFilterChanged?.let {
                value = if (categoryFilterChanged.checked) {
                    filtersValue.copy(categories = filtersValue.categories + it.category)
                } else {
                    filtersValue.copy(categories = filtersValue.categories - it.category)
                }
            }
        }
        addSource(langFilterChanged) { langFilterChanged ->
            langFilterChanged?.let {
                value = if (langFilterChanged.checked) {
                    filtersValue.copy(langs = filtersValue.langs + it.lang)
                } else {
                    filtersValue.copy(langs = filtersValue.langs - it.lang)
                }
            }
        }
        addSource(filterCleared) {
            value = Filters()
        }
    }
    val filtersValue: Filters get() = filters.requireValue()

    private val filteredSessions: LiveData<List<Session>> = sessions
        .combineWith(filters) { sessions, filters ->
            sessions ?: return@combineWith listOf<Session>()
            sessions.filter { session ->
                filters?.isPass(session) ?: true
            }
        }.map { it.orEmpty() }

    val selectedTab: LiveData<SessionPage> = dispatcher
        .subscribe<Action.SessionPageSelected>()
        .map { it.sessionPage }
        .toLiveData(SessionPage.pages[0])

    fun filteredSessionsByDay(day: Int): LiveData<List<Session>> {
        return filteredSessions
            .map { sessions ->
                sessions.orEmpty().filter { session -> session.dayNumber == day }
            }
    }

    fun filteredFavoritedSessions(): LiveData<List<Session>> {
        return filteredSessions
            .map { sessions ->
                sessions.orEmpty()
                    .filter { session -> session.isFavorited }
            }
    }
}
