package io.github.droidkaigi.confsched2019.announcement.ui.actioncreator

import androidx.lifecycle.Lifecycle
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.announcement.ui.di.AnnouncementScope
import io.github.droidkaigi.confsched2019.data.firestore.FireStore
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.coroutineScope
import io.github.droidkaigi.confsched2019.model.LoadingState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class AnnouncementActionCreator @Inject constructor(
    val dispatcher: Dispatcher,
    val fireStore: FireStore,
    @AnnouncementScope val lifecycle: Lifecycle
) : CoroutineScope by lifecycle.coroutineScope {
    fun load() = launch {
        dispatcher.send(Action.AnnouncementLoadingStateChanged(LoadingState.LOADING))
        dispatcher.send(Action.AnnouncementLoaded(fireStore.getAnnouncements()))
        dispatcher.send(Action.AnnouncementLoadingStateChanged(LoadingState.FINISHED))
    }
}
