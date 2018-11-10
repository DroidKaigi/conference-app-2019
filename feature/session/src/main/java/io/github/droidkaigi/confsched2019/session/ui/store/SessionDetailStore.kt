package io.github.droidkaigi.confsched2019.session.ui.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.toLiveData
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.model.Session
import kotlinx.coroutines.channels.filter
import kotlinx.coroutines.channels.map

class SessionDetailStore @AssistedInject constructor(
    dispatcher: Dispatcher,
    @Assisted val sessionId: String
) : ViewModel() {
    @AssistedInject.Factory
    interface Factory {
        fun create(sessionId: String): SessionDetailStore
    }

    val session: LiveData<Session.SpeechSession?> = dispatcher
        .subscribe<Action.SessionLoaded>()
        .filter { it.session.id == sessionId }
        .map { it.session }
        .toLiveData(null)
}
