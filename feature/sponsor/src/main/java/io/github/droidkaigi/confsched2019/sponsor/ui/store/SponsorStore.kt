package io.github.droidkaigi.confsched2019.sponsor.ui.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.toLiveData
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.Sponsor
import kotlinx.coroutines.channels.map
import javax.inject.Inject

class SponsorStore @Inject constructor(
    dispatcher: Dispatcher
) : ViewModel() {
    val loadingState: LiveData<LoadingState> = dispatcher
        .subscribe<Action.SponsorLoadingStateChanged>()
        .map { it.loadingState }
        .toLiveData(LoadingState.LOADING)
    val sponsors: LiveData<List<Sponsor>> = dispatcher
        .subscribe<Action.SponsorLoaded>()
        .map { it.sponsors }
        .toLiveData(listOf())
}
