package io.github.droidkaigi.confsched2019.session.ui.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.toLiveData
import io.github.droidkaigi.confsched2019.model.Action
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.Session
import kotlinx.coroutines.experimental.channels.map
import javax.inject.Inject

class AllSessionsStore @Inject constructor(
    dispatcher: Dispatcher
) : ViewModel() {
    val sessionsLiveData: LiveData<List<Session>> = dispatcher.subscribe<Action.AllSessionLoaded>()
        .map { it.sessions }
        .toLiveData(listOf())
    val loadingStateLiveData: LiveData<LoadingState> = dispatcher.subscribe<Action.AllSessionLoadingStateChanged>()
        .map { it.loadingState }
        .toLiveData(LoadingState.LOADING)
}
