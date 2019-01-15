package io.github.droidkaigi.confsched2019.dispatcher

import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.ext.android.CoroutinePlugin
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.filter
import kotlinx.coroutines.channels.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Dispatcher @Inject constructor() {
    private val _actions = BroadcastChannel<Action>(Channel.CONFLATED)
    val events: ReceiveChannel<Action> get() = _actions.openSubscription()

    inline fun <reified T : Action> subscribe(): ReceiveChannel<T> {
        return events.filter { it is T }.map { it as T }
    }

    suspend fun dispatch(action: Action) {
        // Make sure calling `_actions.send()` from single thread. We can lose action if
        // `_actions.send()` is called simultaneously from multiple threads
        // https://github.com/Kotlin/kotlinx.coroutines/blob/1.0.1/common/kotlinx-coroutines-core-common/src/channels/ConflatedBroadcastChannel.kt#L227-L230
        withContext(CoroutinePlugin.mainDispatcher) {
            _actions.send(action)
        }
    }

    fun launchAndDispatch(action: Action) {
        GlobalScope.launch(CoroutinePlugin.mainDispatcher) {
            _actions.send(action)
        }
    }
}
