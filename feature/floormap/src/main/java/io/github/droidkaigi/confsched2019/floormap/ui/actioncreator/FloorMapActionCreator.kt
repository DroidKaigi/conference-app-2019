package io.github.droidkaigi.confsched2019.floormap.ui.actioncreator

import androidx.lifecycle.Lifecycle
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.coroutineScope
import io.github.droidkaigi.confsched2019.model.LoadingState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class FloorMapActionCreator @Inject constructor(
    val dispatcher: Dispatcher,
    @PageScope val lifecycle: Lifecycle
) : CoroutineScope by lifecycle.coroutineScope {

    fun load() = launch {
        dispatcher.dispatch(Action.FloorMapLoadingStateChanged(LoadingState.LOADING))
        // todo: load something?
        dispatcher.dispatch(Action.FloorMapLoadingStateChanged(LoadingState.LOADED))
    }
}
