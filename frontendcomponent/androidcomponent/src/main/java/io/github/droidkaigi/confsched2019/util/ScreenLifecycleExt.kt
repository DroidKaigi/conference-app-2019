package io.github.droidkaigi.confsched2019.util

import io.github.droidkaigi.confsched2019.ext.android.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * This implementation refers to https://github.com/Kotlin/kotlinx.coroutines/pull/760
 */
fun ScreenLifecycle.createJob(
    state: @ScreenLifecycle.Companion.State Int = ScreenLifecycle.NONE
): Job {
    require(state != ScreenLifecycle.CLEARED) {
        "CLEARED is a terminal state that is forbidden for createJob(…), to avoid leaks."
    }
    return SupervisorJob().also { job ->
        when (state) {
            ScreenLifecycle.CLEARED -> job.cancel()
            else -> GlobalScope.launch(Dispatchers.Main) {
                onCleared {
                    job.cancel()
                }
            }
        }
    }
}
