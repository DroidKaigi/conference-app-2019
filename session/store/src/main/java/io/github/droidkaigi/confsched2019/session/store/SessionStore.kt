package io.github.droidkaigi.confsched2019.session.store

import androidx.lifecycle.LiveData
import confsched2018.droidkaigi.github.io.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.session.ext.toLiveData
import io.github.droidkaigi.confsched2019.session.model.Action
import io.github.droidkaigi.confsched2019.session.model.Session
import kotlinx.coroutines.experimental.channels.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionStore @Inject constructor(
        private val dispatcher: Dispatcher
) {
    val sessionsLiveData: LiveData<List<Session>> = dispatcher.subscrive<Action.SessionsLoaded>()
            .map { it.sessions }
            .toLiveData()
}
