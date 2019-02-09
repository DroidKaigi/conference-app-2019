package io.github.droidkaigi.confsched2019.store

import androidx.lifecycle.ViewModel

open class Store : ViewModel() {
    private val onClearHooks: MutableList<() -> Unit> = mutableListOf()
    fun addHook(hook: () -> Unit) {
        onClearHooks.add(hook)
    }

    override fun onCleared() {
        super.onCleared()
        onClearHooks.forEach { it() }
    }
}
