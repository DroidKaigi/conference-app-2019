package io.github.droidkaigi.confsched2019.announcement.ui.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.toLiveData
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.Post
import kotlinx.coroutines.channels.map
import javax.inject.Inject

class AnnouncementStore @Inject constructor(
    dispatcher: Dispatcher
) : ViewModel() {
    val loadingState: LiveData<LoadingState> = dispatcher
        .subscribe<Action.AnnouncementLoadingStateChanged>()
        .map { it.loadingState }
        .toLiveData(LoadingState.LOADING)
    val posts: LiveData<List<Post>> = dispatcher
        .subscribe<Action.AnnouncementLoaded>()
        .map { it.posts }
        .toLiveData(listOf())
}
