package io.github.droidkaigi.confsched2019.session.ui.actioncreator

import androidx.lifecycle.Lifecycle
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.coroutineScope
import io.github.droidkaigi.confsched2019.model.AudienceCategory
import io.github.droidkaigi.confsched2019.model.Category
import io.github.droidkaigi.confsched2019.model.Lang
import io.github.droidkaigi.confsched2019.model.LangSupport
import io.github.droidkaigi.confsched2019.model.Room
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.SessionPage
import io.github.droidkaigi.confsched2019.system.actioncreator.ErrorHandler
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@PageScope
class SessionPagesActionCreator @Inject constructor(
    override val dispatcher: Dispatcher,
    @PageScope private val lifecycle: Lifecycle
) : CoroutineScope by lifecycle.coroutineScope,
    ErrorHandler {
    fun load(sessions: List<Session>) {
        dispatcher.launchAndDispatch(Action.SessionsLoaded(sessions))
    }

    fun dispatchSessionScrollAdjusted() {
        dispatcher.launchAndDispatch(Action.SessionScrollAdjusted(true))
    }

    fun selectTab(sessionPage: SessionPage) {
        dispatcher.launchAndDispatch(Action.SessionPageSelected(sessionPage))
    }

    fun reselectTab(sessionPage: SessionPage) {
        dispatcher.launchAndDispatch(Action.SessionPageReselected(sessionPage))
    }

    fun changeFilter(room: Room, checked: Boolean) {
        dispatcher.launchAndDispatch(Action.RoomFilterChanged(room, checked))
    }

    fun changeFilter(category: Category, checked: Boolean) {
        dispatcher.launchAndDispatch(Action.CategoryFilterChanged(category, checked))
    }

    fun changeFilter(lang: Lang, checked: Boolean) {
        dispatcher.launchAndDispatch(Action.LangFilterChanged(lang, checked))
    }

    fun changeFilter(langSupport: LangSupport, checked: Boolean) {
        dispatcher.launchAndDispatch(Action.LangSupportFilterChanged(langSupport, checked))
    }

    fun changeFilter(audienceCategory: AudienceCategory, checked: Boolean) {
        dispatcher.launchAndDispatch(
            Action.AudienceCategoryFilterChanged(audienceCategory, checked)
        )
    }

    fun clearFilters() {
        dispatcher.launchAndDispatch(Action.FilterCleared())
    }
}
