package io.github.droidkaigi.confsched2019.widget

import androidx.lifecycle.ViewModel

class PageViewModel : ViewModel() {
    private val lifecycleOwner: PageLifecycleOwner = PageLifecycleOwner()

    init {
        lifecycleOwner.onCreate()
    }

    fun getLifecycle() = lifecycleOwner.lifecycle

    override fun onCleared() {
        lifecycleOwner.onDestroy()
    }
}
