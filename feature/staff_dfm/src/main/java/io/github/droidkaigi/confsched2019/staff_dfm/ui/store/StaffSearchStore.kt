package io.github.droidkaigi.confsched2019.staff_dfm.ui.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.toLiveData
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.StaffContents
import io.github.droidkaigi.confsched2019.model.StaffSearchResult
import io.github.droidkaigi.confsched2019.store.Store
import kotlinx.coroutines.channels.map
import javax.inject.Inject

class StaffSearchStore @Inject constructor(
    dispatcher: Dispatcher
) : Store() {
    val query get() = searchResult.value?.query

    val loadingState: LiveData<LoadingState> = dispatcher
        .subscribe<Action.StaffSearchLoadingStateChanged>()
        .map { it.loadingState }
        .toLiveData(this, LoadingState.LOADING)
    val searchResult = dispatcher
        .subscribe<Action.StaffSearchResultLoaded>()
        .map { it.searchResult }
        .toLiveData(this, StaffSearchResult.EMPTY)
    val staffContents = dispatcher
        .subscribe<Action.StaffLoaded>()
        .map { it.staffContents }
        .toLiveData(this, StaffContents.EMPTY)
}
