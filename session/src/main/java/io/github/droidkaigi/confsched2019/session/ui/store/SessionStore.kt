package io.github.droidkaigi.confsched2019.session.ui.store

import androidx.lifecycle.LiveData
import com.shopify.livedataktx.map
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.toLiveData
import io.github.droidkaigi.confsched2019.model.Action
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.Session
import kotlinx.coroutines.channels.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionStore @Inject constructor(
    dispatcher: Dispatcher
) {
    val sessions: LiveData<List<Session>> = dispatcher
        .subscribe<Action.AllSessionLoaded>()
        .map { it.sessions }
        .toLiveData(listOf())
    val loadingState: LiveData<LoadingState> = dispatcher
        .subscribe<Action.AllSessionLoadingStateChanged>()
        .map { it.loadingState }
        .toLiveData(LoadingState.LOADING)

    fun daySessions(day: Int): LiveData<List<Session>> {
        return sessions
            .map { it.orEmpty().filter { it.dayNumber == day } }
    }

    fun favoriteSessions(): LiveData<List<Session.SpeechSession>> {
        return sessions
            .map {
                it.orEmpty().filterIsInstance<Session.SpeechSession>()
                    .filter { it.isFavorited }
            }
    }

    fun session(sessionId: String): LiveData<Session> {
        return sessions
            .map {
                it.orEmpty().filterIsInstance<Session.SpeechSession>()
                .first{it.id == sessionId}
            }
    }

}
