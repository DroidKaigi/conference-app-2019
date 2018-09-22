package io.github.droidkaigi.confsched2019.session.ui.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import confsched2018.droidkaigi.github.io.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.toLiveData
import io.github.droidkaigi.confsched2019.model.Action
import io.github.droidkaigi.confsched2019.model.Session
import kotlinx.coroutines.experimental.channels.map
import javax.inject.Inject

class AllSessionsStore @Inject constructor(
        dispatcher: Dispatcher
) : ViewModel() {
    val sessionsLiveData: LiveData<List<Session>> = dispatcher.subscribe<Action.AllSessionLoaded>()
            .map { it.sessions }
            .toLiveData()
}
