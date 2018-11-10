package io.github.droidkaigi.confsched2019.announcement.ui.actioncreator

import androidx.lifecycle.Lifecycle
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.coroutineScope
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.model.LoadingState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class AnnouncementActionCreator @Inject constructor(
    val dispatcher: Dispatcher,
    @Named("AnnouncementFragment") val lifecycle: Lifecycle
) : CoroutineScope by lifecycle.coroutineScope {
    fun load() = launch {
        dispatcher.send(Action.AnnouncementLoadingStateChanged(LoadingState.LOADING))
        dispatcher.send(Action.AnnouncementLoadingStateChanged(LoadingState.FINISHED))
    }
}
