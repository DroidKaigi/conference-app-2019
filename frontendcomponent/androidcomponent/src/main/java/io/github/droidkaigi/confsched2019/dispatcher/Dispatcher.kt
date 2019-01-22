package io.github.droidkaigi.confsched2019.dispatcher

import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.ext.android.CoroutinePlugin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class Dispatcher @Inject constructor() {
    private val _actions = BroadcastChannel<Action>(Channel.CONFLATED)
    val events: ReceiveChannel<Action> get() = _actions.openSubscription()

    inline fun <reified T : Action> subscribe(): ReceiveChannel<T> {
        return events.filterAndCast()
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

    inline fun <reified E, reified R : E> ReceiveChannel<E>.filterAndCast(
        context: CoroutineContext = Dispatchers.Unconfined
    ): ReceiveChannel<R> =
        GlobalScope.produce(context, capacity = Channel.UNLIMITED) {
            consumeEach { e ->
                (e as? R)?.let { send(it) }
            }
        }
}
