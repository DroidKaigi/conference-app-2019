package io.github.droidkaigi.confsched2019.session.ui.actioncreator

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.data.repository.SessionRepository
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.coroutineScope
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.Speaker
import io.github.droidkaigi.confsched2019.system.actioncreator.ErrorHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SpeakerActionCreator @Inject constructor(
    override val dispatcher: Dispatcher,
    val sessionRepository: SessionRepository,
    @PageScope val lifecycle: Lifecycle
) : CoroutineScope by lifecycle.coroutineScope,
    ErrorHandler {

    fun load(speakerId: String) = launch {
        try {
            val speaker = newSpeaker(speakerId)
            dispatcher.dispatch(Action.SpeakerLoaded(speaker))
        } catch (e: Exception) {
            onError(e)
        }
    }

    private suspend fun newSpeaker(
        speakerId: String
    ): Speaker {
        return withContext(Dispatchers.Default) {
            sessionRepository
                .sessionContents()
                .sessions
                .filterIsInstance<Session.SpeechSession>()
                .flatMap { it.speakers }
                .first { it.id == speakerId }
        }
    }
}
