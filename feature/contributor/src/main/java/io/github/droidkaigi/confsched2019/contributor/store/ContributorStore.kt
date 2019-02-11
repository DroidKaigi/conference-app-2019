package io.github.droidkaigi.confsched2019.contributor.store

import androidx.lifecycle.LiveData
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.toLiveData
import io.github.droidkaigi.confsched2019.model.ContributorContents
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.store.Store
import kotlinx.coroutines.channels.map
import javax.inject.Inject

class ContributorStore @Inject constructor(
    dispatcher: Dispatcher
) : Store() {
    val loadingState: LiveData<LoadingState> = dispatcher
        .subscribe<Action.ContributorLoadingStateChanged>()
        .map { it.loadingState }
        .toLiveData(this, LoadingState.LOADING)

    val contributors: LiveData<ContributorContents> = dispatcher
        .subscribe<Action.ContributorLoaded>()
        .map { it.contributors }
        .toLiveData(this, ContributorContents.EMTPY)
}
