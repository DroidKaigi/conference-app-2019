package io.github.droidkaigi.confsched2019.session.ui.actioncreator

import androidx.lifecycle.LifecycleOwner
import io.github.droidkaigi.confsched2019.data.repository.SessionRepository
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.toCoroutineScope
import io.github.droidkaigi.confsched2019.model.Action
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.Session
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class AllSessionActionCreator @Inject constructor(
    val dispatcher: Dispatcher,
    val sessionRepository: SessionRepository,
    @Named("AllSessionsFragment") val lifecycleOwner: LifecycleOwner
) : CoroutineScope by lifecycleOwner.toCoroutineScope(){
    fun load() = launch(coroutineContext) {
        try {
            // listen session state
            val sessionChannel = sessionRepository.sessionChannel()
            sessionChannel.consumeEach { sessions ->
                dispatcher.send(Action.AllSessionLoaded(sessions))
            }
        } catch (e: Exception) {
            // TODO: Error Handling
            e.printStackTrace()
        }
    }

    fun toggleFavorite(session: Session.SpeechSession) {
        launch {
            try {
                dispatcher.send(Action.AllSessionLoadingStateChanged(LoadingState.LOADING))
                sessionRepository.toggleFavorite(session)
                dispatcher.send(Action.AllSessionLoadingStateChanged(LoadingState.FINISHED))
            } catch (e: Exception) {
                // TODO: Error Handling
                e.printStackTrace()
            }
        }
    }
}
