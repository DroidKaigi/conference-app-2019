package io.github.droidkaigi.confsched2019.session.store

import androidx.lifecycle.LiveData
import confsched2018.droidkaigi.github.io.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.session.model.Action
import io.github.droidkaigi.confsched2019.session.model.Session
import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.consume
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.channels.map
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.experimental.CoroutineContext

@Singleton
class SessionStore @Inject constructor(
        private val dispatcher: Dispatcher
) {
    val sessionsLiveData: LiveData<List<Session>> = object :LiveData<List<Session>>(){
        lateinit var job:Job
        override fun onActive() {
            super.onActive()
            job = dispatcher.subscrive<Action.SessionsLoaded>().launchConsumeEach(UI) { postValue(it.sessions) }
        }

        override fun onInactive() {
            super.onInactive()
            job.cancel()
        }
    }
}
fun <E> ReceiveChannel<E>.launchConsumeEach(context: CoroutineContext = DefaultDispatcher,
                                            start: CoroutineStart = CoroutineStart.DEFAULT,
                                            parent: Job? = null,
                                            action: (E) -> Unit) = launch(context, start, parent) {
    consume {
        for (element in this) action(element)
    }
}