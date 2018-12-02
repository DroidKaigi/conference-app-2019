package io.github.droidkaigi.confsched2019.widget.component

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

class TestLifecycleOwner : LifecycleOwner {

    private val lifecycle: LifecycleRegistry = LifecycleRegistry(this)

    override fun getLifecycle(): Lifecycle = lifecycle

    fun handleEvent(event: Lifecycle.Event) = apply {
        lifecycle.handleLifecycleEvent(event)
    }
}
