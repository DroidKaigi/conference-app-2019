package io.github.droidkaigi.confsched2019.session.ui.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.shopify.livedataktx.combineWith
import com.shopify.livedataktx.map
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.requireValue
import io.github.droidkaigi.confsched2019.ext.toLiveData
import io.github.droidkaigi.confsched2019.ext.toSingleLiveData
import io.github.droidkaigi.confsched2019.model.Filters
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.SessionPage
import io.github.droidkaigi.confsched2019.store.Store
import kotlinx.coroutines.channels.map
import javax.inject.Inject

class SessionPagesStore @Inject constructor(
    dispatcher: Dispatcher
) : Store() {

    private val sessions: LiveData<List<Session>> = dispatcher
        .subscribe<Action.SessionsLoaded>()
        .map { it.sessions }
        .toLiveData(this, listOf())

    private val roomFilterChanged = dispatcher
        .subscribe<Action.RoomFilterChanged>()
        .toLiveData(this)

    private val categoryFilterChanged = dispatcher
        .subscribe<Action.CategoryFilterChanged>()
        .toLiveData(this)

    private val langFilterChanged = dispatcher
        .subscribe<Action.LangFilterChanged>()
        .toLiveData(this)

    private val langSupportFilterChanged = dispatcher
        .subscribe<Action.LangSupportFilterChanged>()
        .toLiveData(this)

    private val audienceCategoryFilterChanged = dispatcher
        .subscribe<Action.AudienceCategoryFilterChanged>()
        .toLiveData(this)

    private val filterCleared = dispatcher
        .subscribe<Action.FilterCleared>()
        .toLiveData(this)

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
        addSource(langSupportFilterChanged) { langSupportFilterChanged ->
            langSupportFilterChanged?.let {
                value = if (langSupportFilterChanged.checked) {
                    filtersValue.copy(langSupports = filtersValue.langSupports + it.langSupport)
                } else {
                    filtersValue.copy(langSupports = filtersValue.langSupports - it.langSupport)
                }
            }
        }
        addSource(audienceCategoryFilterChanged) { audienceCategoryFilterChanged ->
            audienceCategoryFilterChanged?.let {
                value = if (audienceCategoryFilterChanged.checked) {
                    filtersValue.copy(
                        audienceCategories = filtersValue.audienceCategories + it.audienceCategory
                    )
                } else {
                    filtersValue.copy(
                        audienceCategories = filtersValue.audienceCategories - it.audienceCategory
                    )
                }
            }
        }

        addSource(filterCleared) {
            value = Filters()
        }
    }
    val filtersValue: Filters get() = filters.requireValue()

    val filteredSessions: LiveData<List<Session>> = sessions
        .combineWith(filters) { sessions, filters ->
            sessions ?: return@combineWith listOf<Session>()
            sessions.filter { session ->
                filters?.isPass(session) ?: true
            }
        }.map { it.orEmpty() }

    val selectedTab: LiveData<SessionPage> = dispatcher
        .subscribe<Action.SessionPageSelected>()
        .map { it.sessionPage }
        .toLiveData(this, SessionPage.pages[0])

    val reselectedTab: LiveData<SessionPage> = dispatcher
        .subscribe<Action.SessionPageReselected>()
        .map { it.sessionPage }
        .toSingleLiveData(this, SessionPage.pages[0])

    val sessionScrollAdjusted: LiveData<Boolean> = dispatcher
        .subscribe<Action.SessionScrollAdjusted>()
        .map { it.adjusted }
        .toSingleLiveData(this, false)

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
