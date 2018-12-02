package io.github.droidkaigi.confsched2019.ext.android

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import java.util.concurrent.ConcurrentHashMap

/**
 * from: https://github.com/Kotlin/kotlinx.coroutines/pull/760
 * Modify to use [CoroutinePlugin] for testing
 * Returns a [CoroutineScope] that uses [Dispatchers.Main] by default, and that is cancelled when
 * the [Lifecycle] reaches [Lifecycle.State.DESTROYED] state.
 *
 * Note that this value is cached until the Lifecycle reaches the destroyed state.
 */
val Lifecycle.coroutineScope: CoroutineScope
    get() = cachedLifecycleCoroutineScopes[this] ?: job.let { job ->
        val newScope = CoroutineScope(job + CoroutinePlugin.mainDispatcher)
        if (job.isActive) {
            cachedLifecycleCoroutineScopes[this] = newScope
            job.invokeOnCompletion { _ -> cachedLifecycleCoroutineScopes -= this }
        }
        newScope
    }

/**
 * Calls [Lifecycle.coroutineScope] for the [Lifecycle] of this [LifecycleOwner].
 *
 * This is an inline property, just there for convenient usage from any [LifecycleOwner],
 * like FragmentActivity, AppCompatActivity, Fragment and LifecycleService.
 */
inline val LifecycleOwner.coroutineScope get() = lifecycle.coroutineScope

/**
 * Returns a [SupervisorJob] that will be cancelled as soon as the [Lifecycle] reaches
 * [Lifecycle.State.DESTROYED] state.
 *
 * Note that this value is cached until the Lifecycle reaches the destroyed state.
 *
 * You can use this job for custom [CoroutineScope]s, or as a parent [Job].
 */
val Lifecycle.job: Job
    get() = cachedLifecycleJobs[this] ?: createJob().also {
        if (it.isActive) {
            cachedLifecycleJobs[this] = it
            it.invokeOnCompletion { _ -> cachedLifecycleJobs -= this }
        }
    }

private val cachedLifecycleJobs = ConcurrentHashMap<Lifecycle, Job>()
private val cachedLifecycleCoroutineScopes = ConcurrentHashMap<Lifecycle, CoroutineScope>()
