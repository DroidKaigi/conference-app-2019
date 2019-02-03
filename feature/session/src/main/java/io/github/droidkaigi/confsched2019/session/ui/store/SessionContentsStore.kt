package io.github.droidkaigi.confsched2019.session.ui.store

import androidx.lifecycle.LiveData
import com.shopify.livedataktx.map
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.mapNotNull
import io.github.droidkaigi.confsched2019.ext.android.requireValue
import io.github.droidkaigi.confsched2019.ext.android.toLiveData
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.ServiceSession
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.SessionContents
import io.github.droidkaigi.confsched2019.model.Speaker
import io.github.droidkaigi.confsched2019.model.SpeechSession
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
    val isInitialized: Boolean get() = loadingState.value == LoadingState.INITIALIZED
    val isLoading get() = loadingState.value == LoadingState.LOADING
    val isLoaded: Boolean get() = loadingState.value == LoadingState.LOADED

    val sessionContents = dispatcher
        .subscribe<Action.SessionContentsLoaded>()
        .map { it.sessionContents }
        .toLiveData(SessionContents.EMPTY)
    val sessions get() = sessionContents.requireValue().sessions
    val langs get() = sessionContents.requireValue().langs
    val categorys get() = sessionContents.requireValue().category
    val rooms get() = sessionContents.requireValue().rooms
    val langSupports get() = sessionContents.requireValue().langSupports
    val audienceCategories get() = sessionContents.requireValue().audienceCategories

    fun speechSession(sessionId: String): LiveData<SpeechSession> =
        sessionContents.mapNotNull { sessionContents ->
            sessionContents
                ?.sessions
                ?.find { it.id == sessionId } as? SpeechSession
        }

    fun speechSessionBySpeakerName(speakerId: String): LiveData<SpeechSession> =
        sessionContents.mapNotNull { sessionContents ->
            sessionContents
                ?.sessions
                ?.filterIsInstance<SpeechSession>()
                ?.find { it.speakers.find { it.id == speakerId } != null }
        }

    fun speaker(speakerId: String): LiveData<Speaker> =
        sessionContents.mapNotNull { sessionContents ->
            sessionContents
                ?.speakers
                ?.find { it.id == speakerId }
        }

    fun serviceSession(sessionId: String): LiveData<ServiceSession> =
        sessionContents.mapNotNull { sessionContents ->
            sessionContents
                ?.sessions
                ?.findLast { it.id == sessionId } as? ServiceSession
        }

    fun sessionsByDay(day: Int): LiveData<List<Session>> {
        return sessionContents
            .mapNotNull { it?.sessions }
            .map { sessions ->
                sessions.orEmpty().filter { session -> session.dayNumber == day }
            }
    }
}
