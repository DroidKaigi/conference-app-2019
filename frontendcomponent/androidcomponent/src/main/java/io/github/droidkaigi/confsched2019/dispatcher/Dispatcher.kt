package io.github.droidkaigi.confsched2019.dispatcher

import io.github.droidkaigi.confsched2019.action.Action
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.filter
import kotlinx.coroutines.channels.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Singleton
class Dispatcher @Inject constructor() {
    private val _actions = BroadcastChannel<Action>(100)
    val events: ReceiveChannel<Action> get() = _actions.openSubscription()

    inline fun <reified T : Action> subscribe(): ReceiveChannel<T> {
        return events.filter { it is T }.map { it as T }
    }

    suspend fun send(action: Action) {
        _actions.send(action)
    }

    fun launchAndSend(action: Action) {
        GlobalScope.launch {
            _actions.send(action)
        }
    }
}
