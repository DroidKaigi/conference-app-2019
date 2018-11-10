package io.github.droidkaigi.confsched2019.session.ui.actioncreator

import androidx.lifecycle.LifecycleOwner
import io.github.droidkaigi.confsched2019.data.repository.SessionRepository
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.coroutineScope
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.model.Session
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class SessionDetailActionCreator @Inject constructor(
    val dispatcher: Dispatcher,
    val sessionRepository: SessionRepository,
    @Named("SessionDetailFragment") val lifecycleOwner: LifecycleOwner
) : CoroutineScope by lifecycleOwner.coroutineScope {

    fun load(sessionId: String) = launch {
        try {
            val session = newSession(sessionId)
            dispatcher.send(Action.SessionLoaded(session))
        } catch (e: Exception) {
            // TODO: Error Handling
            throw e
        }
    }

    fun toggleFavorite(session: Session.SpeechSession) {
        launch {
            try {
                sessionRepository.toggleFavorite(session)
                dispatcher.send(
                    Action.SessionLoaded(
                        session.copy(isFavorited = !session.isFavorited)
                    )
                )
            } catch (e: Exception) {
                // TODO: error handling
                throw e
            }
        }
    }

    private suspend fun newSession(
        sessionId: String
    ): Session.SpeechSession {
        val sessions = sessionRepository.sessions()
        val session = sessions
            .filterIsInstance<Session.SpeechSession>()
            .first { it.id == sessionId }
        return session
    }
}
