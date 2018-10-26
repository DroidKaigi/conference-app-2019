package io.github.droidkaigi.confsched2019.session.ui.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.shopify.livedataktx.map
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.toLiveData
import io.github.droidkaigi.confsched2019.model.Action
import io.github.droidkaigi.confsched2019.model.Session
import kotlinx.coroutines.channels.map
import javax.inject.Inject

class AllSessionsStore @Inject constructor(
    dispatcher: Dispatcher
) : ViewModel() {
    val sessions: LiveData<List<Session>> = dispatcher
        .subscribe<Action.AllSessionLoaded>()
        .map { it.sessions }
        .toLiveData(listOf())

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
}
