package io.github.droidkaigi.confsched2019.session.ui.store

import androidx.lifecycle.LiveData
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.mapNotNull
import io.github.droidkaigi.confsched2019.ext.android.requireValue
import io.github.droidkaigi.confsched2019.ext.android.toLiveData
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.SessionContents
import io.github.droidkaigi.confsched2019.model.Speaker
import kotlinx.coroutines.channels.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionContentsStore @Inject constructor(
    dispatcher: Dispatcher
) {
    val loadingState = dispatcher
        .subscribe<Action.SessionLoadingStateChanged>()
        .map { it.loadingState }
        .toLiveData(LoadingState.INITIALIZED)
    val isInitialized: Boolean
        get() = loadingState.value == LoadingState.INITIALIZED
    val isLoading
        get() = loadingState.value == LoadingState.LOADING
    val isLoaded: Boolean
        get() = loadingState.value == LoadingState.LOADED

    val sessionContents = dispatcher
        .subscribe<Action.SessionContentsLoaded>()
        .map { it.sessionContents }
        .toLiveData(SessionContents.EMPTY)

    val sessions get() = sessionContents.requireValue().sessions
    val langs get() = sessionContents.requireValue().langs
    val topics get() = sessionContents.requireValue().topics
    val rooms get() = sessionContents.requireValue().rooms

    fun speechSession(sessionId: String): LiveData<Session.SpeechSession> =
        sessionContents.mapNotNull { sessionContents ->
            sessionContents
                ?.sessions
                ?.find { it.id == sessionId } as? Session.SpeechSession
        }

    fun speechSessionBySpeakerName(speakerId: String): LiveData<Session.SpeechSession> =
        sessionContents.mapNotNull { sessionContents ->
            sessionContents
                ?.sessions
                ?.filterIsInstance<Session.SpeechSession>()
                ?.find { it.speakers.find { it.id == speakerId } != null }
        }

    fun speaker(speakerId: String): LiveData<Speaker> =
        sessionContents.mapNotNull { sessionContents ->
            sessionContents
                ?.speakers
                ?.find { it.id == speakerId }
        }
}
