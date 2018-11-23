package io.github.droidkaigi.confsched2019.session.ui.store

import androidx.lifecycle.LiveData
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.toLiveData
import io.github.droidkaigi.confsched2019.model.LoadingState
import kotlinx.coroutines.channels.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionsStore @Inject constructor(
    dispatcher: Dispatcher
) {
    val loadingState: LiveData<LoadingState> = dispatcher
        .subscribe<Action.SessionRefreshStateChanged>()
        .map { it.loadingState }
        .toLiveData(LoadingState.LOADING)
    val isLoading
        get() = loadingState.value == LoadingState.LOADING
}
