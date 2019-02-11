package io.github.droidkaigi.confsched2019.staff_dfm.ui.store

import androidx.lifecycle.LiveData
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.toLiveData
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.StaffContents
import io.github.droidkaigi.confsched2019.model.StaffSearchResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.map
import javax.inject.Inject

@PageScope
class StaffSearchStore @Inject constructor(
    dispatcher: Dispatcher,
    @PageScope coroutineScope: CoroutineScope
) {
    val query get() = searchResult.value?.query

    val loadingState: LiveData<LoadingState> = dispatcher
        .subscribe<Action.StaffSearchLoadingStateChanged>()
        .map { it.loadingState }
        .toLiveData(coroutineScope, LoadingState.LOADING)
    val searchResult = dispatcher
        .subscribe<Action.StaffSearchResultLoaded>()
        .map { it.searchResult }
        .toLiveData(coroutineScope, StaffSearchResult.EMPTY)
    val staffContents = dispatcher
        .subscribe<Action.StaffLoaded>()
        .map { it.staffContents }
        .toLiveData(coroutineScope, StaffContents.EMPTY)
}
