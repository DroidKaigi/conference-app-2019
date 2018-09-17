package io.github.droidkaigi.confsched2019.session.ui.actioncreator

import androidx.lifecycle.LifecycleOwner
import confsched2018.droidkaigi.github.io.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.data.repository.SessionRepository
import io.github.droidkaigi.confsched2019.ext.android.toCoroutineScope
import io.github.droidkaigi.confsched2019.session.model.Action
import io.github.droidkaigi.confsched2019.session.model.Session
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class AllSessionActionCreator @Inject constructor(
        val dispatcher: Dispatcher,
        val sessionRepository: SessionRepository,
        val lifecycleOwner: LifecycleOwner
) : CoroutineScope by lifecycleOwner.toCoroutineScope() {
    fun load() {
        launch {
            val sessions = sessionRepository.sessions()
            dispatcher.send(Action.AllSessionLoaded(sessions))
        }
    }

    fun toggleFavorite(session: Session.SpeechSession) {
        launch {
            sessionRepository.toggleFavorite(session)
            val sessions = sessionRepository.sessions()
            dispatcher.send(Action.AllSessionLoaded(sessions))
        }
    }
}

