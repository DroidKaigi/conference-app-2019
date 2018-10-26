package io.github.droidkaigi.confsched2019.session.ui.actioncreator

import androidx.lifecycle.LifecycleOwner
import io.github.droidkaigi.confsched2019.data.repository.SessionRepository
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.toCoroutineScope
import io.github.droidkaigi.confsched2019.model.Action
import io.github.droidkaigi.confsched2019.model.Session
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class SessionDetailActionCreator @Inject constructor(
    val dispatcher: Dispatcher,
    val sessionRepository: SessionRepository,
    @Named("SessionDetailFragment") val lifecycleOwner: LifecycleOwner
) : CoroutineScope by lifecycleOwner.toCoroutineScope() {
    fun load(sessionId: String) {
        launch {
            try {
                // listen session state
                val sessionChannel = sessionRepository.sessionChannel()
                sessionChannel.consumeEach { sessions ->
                    val session = sessions
                        .filterIsInstance<Session.SpeechSession>()
                        .first { it.id == sessionId }
                    dispatcher.send(Action.SessionLoaded(session))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
