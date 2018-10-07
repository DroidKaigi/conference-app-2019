package io.github.droidkaigi.confsched2019.ext.android

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

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
