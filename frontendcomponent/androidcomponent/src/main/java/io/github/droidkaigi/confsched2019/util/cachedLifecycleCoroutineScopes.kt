package io.github.droidkaigi.confsched2019.util

import io.github.droidkaigi.confsched2019.ext.android.Dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import java.util.concurrent.ConcurrentHashMap

private val cachedLifecycleCoroutineScopes = ConcurrentHashMap<ScreenLifecycle, CoroutineScope>()
private val cachedLifecycleJobs = ConcurrentHashMap<ScreenLifecycle, Job>()

val ScreenLifecycle.coroutineScope: CoroutineScope
    get() = cachedLifecycleCoroutineScopes[this] ?: job.let { job ->
        val newScope = CoroutineScope(job + Dispatchers.Main)
        if (job.isActive) {
            cachedLifecycleCoroutineScopes[this] = newScope
            job.invokeOnCompletion { cachedLifecycleCoroutineScopes -= this }
        }
        newScope
    }

val ScreenLifecycle.job: Job
    get() = cachedLifecycleJobs[this] ?: createJob().also {
        if (it.isActive) {
            cachedLifecycleJobs[this] = it
            it.invokeOnCompletion { cachedLifecycleJobs -= this }
        }
    }
