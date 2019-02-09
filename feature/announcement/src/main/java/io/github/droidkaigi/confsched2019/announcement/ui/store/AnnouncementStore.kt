package io.github.droidkaigi.confsched2019.announcement.ui.store

import androidx.lifecycle.LiveData
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.toLiveData
import io.github.droidkaigi.confsched2019.model.Announcement
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.store.Store
import kotlinx.coroutines.channels.map
import javax.inject.Inject

class AnnouncementStore @Inject constructor(
    dispatcher: Dispatcher
) : Store() {
    val loadingState: LiveData<LoadingState> = dispatcher
        .subscribe<Action.AnnouncementLoadingStateChanged>()
        .map { it.loadingState }
        .toLiveData(this, LoadingState.INITIALIZED)
    val announcements: LiveData<List<Announcement>> = dispatcher
        .subscribe<Action.AnnouncementLoaded>()
        .map { it.announcements }
        .toLiveData(this, listOf())
}
