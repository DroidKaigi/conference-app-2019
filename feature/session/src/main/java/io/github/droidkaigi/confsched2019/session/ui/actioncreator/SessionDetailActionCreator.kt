package io.github.droidkaigi.confsched2019.session.ui.actioncreator

import androidx.lifecycle.LifecycleOwner
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.data.repository.SessionRepository
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.coroutineScope
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.system.actioncreator.ErrorHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class SessionDetailActionCreator @Inject constructor(
    override val dispatcher: Dispatcher,
    val sessionRepository: SessionRepository,
    @Named("SessionDetailFragment") val lifecycleOwner: LifecycleOwner
) : CoroutineScope by lifecycleOwner.coroutineScope,
    ErrorHandler {

    fun load(sessionId: String) {
        launch {
            try {
                val session = getSession(sessionId)
                dispatcher.dispatch(Action.SessionLoaded(session))
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun toggleFavorite(session: Session.SpeechSession) {
        launch {
            try {
                sessionRepository.toggleFavorite(session)
                dispatcher.dispatch(
                    Action.SessionLoaded(
                        session.copy(isFavorited = !session.isFavorited)
                    )
                )
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    private suspend fun getSession(
        sessionId: String
    ): Session.SpeechSession {
        val sessions = sessionRepository.sessionContents().sessions
        return sessions
            .filterIsInstance<Session.SpeechSession>()
            .first { it.id == sessionId }
    }
}
