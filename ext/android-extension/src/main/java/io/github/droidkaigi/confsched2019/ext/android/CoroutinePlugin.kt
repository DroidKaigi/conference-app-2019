package io.github.droidkaigi.confsched2019.ext.android

import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

object CoroutinePlugin {

    private val defaultIoDispatcher: CoroutineContext = Dispatchers.IO
    val ioDispatcher: CoroutineContext
        get() = ioDispatcherHandler?.let { handler ->
            handler(defaultIoDispatcher)
        } ?: defaultIoDispatcher
    @set:VisibleForTesting
    var ioDispatcherHandler: ((CoroutineContext) -> CoroutineContext)? = null

    private val defaultComputationDispatcher: CoroutineContext = Dispatchers.Default
    val defaultDispatcher: CoroutineContext
        get() = computationDispatcherHandler?.let { handler ->
            handler(defaultComputationDispatcher)
        } ?: defaultComputationDispatcher
    @set:VisibleForTesting
    var computationDispatcherHandler: ((CoroutineContext) -> CoroutineContext)? = null

    private val defaultMainDispatcher: CoroutineContext = Dispatchers.Main
    val mainDispatcher: CoroutineContext
        get() = mainDispatcherHandler?.let { handler ->
            handler(defaultMainDispatcher)
        } ?: defaultMainDispatcher
    @set:VisibleForTesting
    var mainDispatcherHandler: ((CoroutineContext) -> CoroutineContext)? = null

    @VisibleForTesting @JvmStatic fun reset() {
        ioDispatcherHandler = null
        computationDispatcherHandler = null
        mainDispatcherHandler = null
    }
}
