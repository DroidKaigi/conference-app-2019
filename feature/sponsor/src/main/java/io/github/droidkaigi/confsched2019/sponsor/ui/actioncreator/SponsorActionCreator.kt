package io.github.droidkaigi.confsched2019.sponsor.ui.actioncreator

import androidx.lifecycle.Lifecycle
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.coroutineScope
import io.github.droidkaigi.confsched2019.model.LoadingState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class SponsorActionCreator @Inject constructor(
    val dispatcher: Dispatcher,
    @PageScope val lifecycle: Lifecycle
) : CoroutineScope by lifecycle.coroutineScope {

    fun load() = launch {
        dispatcher.dispatch(Action.SponsorLoadingStateChanged(LoadingState.LOADING))
        dispatcher.dispatch(Action.SponsorLoaded(listOf())) // TODO
        dispatcher.dispatch(Action.SponsorLoadingStateChanged(LoadingState.LOADED))
    }
}
