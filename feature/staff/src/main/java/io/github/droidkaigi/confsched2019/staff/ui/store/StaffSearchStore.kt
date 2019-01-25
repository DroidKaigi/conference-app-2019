package io.github.droidkaigi.confsched2019.staff.ui.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.toLiveData
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.StaffContents
import io.github.droidkaigi.confsched2019.model.StaffSearchResult
import kotlinx.coroutines.channels.map
import javax.inject.Inject

class StaffSearchStore @Inject constructor(
    dispatcher: Dispatcher
) : ViewModel() {
    val query get() = searchResult.value?.query

    val loadingState: LiveData<LoadingState> = dispatcher
        .subscribe<Action.StaffSearchLoadingStateChanged>()
        .map { it.loadingState }
        .toLiveData(LoadingState.LOADING)
    val searchResult = dispatcher
        .subscribe<Action.StaffSearchResultLoaded>()
        .map { it.searchResult }
        .toLiveData(StaffSearchResult.EMPTY)
    val staffContents = dispatcher
        .subscribe<Action.StaffLoaded>()
        .map { it.staffContents }
        .toLiveData(StaffContents.EMPTY)
}
