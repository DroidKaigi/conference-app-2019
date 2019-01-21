package io.github.droidkaigi.confsched2019.ui

import androidx.lifecycle.ViewModel
import io.github.droidkaigi.confsched2019.util.ScreenLifecycle

class ScreenViewModel : ViewModel() {

    val lifecycle: ScreenLifecycle = ScreenLifecycle()

    init {
        lifecycle.dispatchOnInit()
    }

    override fun onCleared() {
        lifecycle.dispatchOnCleared()
    }
}
