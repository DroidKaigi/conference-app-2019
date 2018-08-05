package io.github.droidkaigi.confsched2019.session.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import confsched2018.droidkaigi.github.io.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.session.ext.toLiveData
import io.github.droidkaigi.confsched2019.session.model.Action
import io.github.droidkaigi.confsched2019.session.model.Session
import kotlinx.coroutines.experimental.channels.map
import javax.inject.Inject

class AllSessionsStore @Inject constructor(
        dispatcher: Dispatcher
) : ViewModel() {
    val sessionsLiveData: LiveData<List<Session>> = dispatcher.subscrive<Action.AllSessionLoaded>()
            .map { it.sessions }
            .toLiveData()
}