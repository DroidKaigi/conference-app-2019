package io.github.droidkaigi.confsched2019.session.ui.store

import androidx.lifecycle.LiveData
import com.shopify.livedataktx.map
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.toLiveData
import io.github.droidkaigi.confsched2019.model.Action
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.Session
import kotlinx.coroutines.channels.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionStore @Inject constructor(
    dispatcher: Dispatcher
) {

    val loadingState: LiveData<LoadingState> = dispatcher
        .subscribe<Action.AllSessionLoadingStateChanged>()
        .map { it.loadingState }
        .toLiveData(LoadingState.LOADING)
}
