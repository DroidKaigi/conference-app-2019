package io.github.droidkaigi.confsched2019.ext.android

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.Job
import kotlin.coroutines.experimental.CoroutineContext

private class LifecycleCoroutineScope(
    lifecycleOwner: LifecycleOwner,
    private val coroutineDispatcher: CoroutineDispatcher
) : CoroutineScope, LifecycleObserver {
    private val job: Job
    override val coroutineContext: CoroutineContext get() = coroutineDispatcher + job

    init {
        job = Job()
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        job.cancel()
    }
}

fun LifecycleOwner.toCoroutineScope(dispatcher: CoroutineDispatcher = Dispatchers.Default): CoroutineScope {
    return LifecycleCoroutineScope(this, dispatcher)
}
