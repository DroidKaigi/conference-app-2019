package io.github.droidkaigi.confsched2019.session.ui.store

import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.toLiveData
import io.github.droidkaigi.confsched2019.model.SearchResult
import io.github.droidkaigi.confsched2019.store.Store
import kotlinx.coroutines.channels.map
import javax.inject.Inject

class SearchStore @Inject constructor(
    dispatcher: Dispatcher
) : Store() {
    val query get() = searchResult.value?.query

    val searchResult = dispatcher
        .subscribe<Action.SearchResultLoaded>()
        .map { it.searchResult }
        .toLiveData(this, SearchResult.EMPTY)
}
