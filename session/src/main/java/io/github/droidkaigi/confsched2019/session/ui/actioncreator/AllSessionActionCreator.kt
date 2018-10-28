package io.github.droidkaigi.confsched2019.session.ui.actioncreator

import androidx.lifecycle.Lifecycle
import io.github.droidkaigi.confsched2019.data.repository.SessionRepository
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.coroutineScope
import io.github.droidkaigi.confsched2019.model.Action
import io.github.droidkaigi.confsched2019.model.SessionTab
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class AllSessionActionCreator @Inject constructor(
    val dispatcher: Dispatcher,
    val sessionRepository: SessionRepository,
    @Named("AllSessionsFragment") val lifecycle: Lifecycle
) : CoroutineScope by lifecycle.coroutineScope{
    fun load() = launch {
        try {
            // listen session state
            val sessionChannel = sessionRepository.sessionChannel()
            sessionChannel.consumeEach { sessions ->
                dispatcher.send(Action.AllSessionLoaded(sessions))
            }
        } catch (e: Exception) {
            // TODO: Error Handling
            throw e
        }
    }

    fun selectTab(sessionTab: SessionTab) {
        dispatcher.launchAndSend(Action.SessionTabSelected(sessionTab))
    }
}
