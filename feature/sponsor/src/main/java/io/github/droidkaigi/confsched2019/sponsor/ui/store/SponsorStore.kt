package io.github.droidkaigi.confsched2019.sponsor.ui.store

import androidx.lifecycle.LiveData
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.toLiveData
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.SponsorCategory
import io.github.droidkaigi.confsched2019.store.Store
import kotlinx.coroutines.channels.map
import javax.inject.Inject

class SponsorStore @Inject constructor(
    dispatcher: Dispatcher
) : Store() {
    val loadingState: LiveData<LoadingState> = dispatcher
        .subscribe<Action.SponsorLoadingStateChanged>()
        .map { it.loadingState }
        .toLiveData(this, LoadingState.LOADING)
    val sponsors: LiveData<List<SponsorCategory>> = dispatcher
        .subscribe<Action.SponsorLoaded>()
        .map { it.sponsors }
        .toLiveData(this, listOf())
}
