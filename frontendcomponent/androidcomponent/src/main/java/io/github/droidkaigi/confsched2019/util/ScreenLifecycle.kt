package io.github.droidkaigi.confsched2019.util

import androidx.annotation.IntDef

class ScreenLifecycle {

    companion object {
        const val NONE = 0
        const val INIT = 1
        const val CLEARED = 2

        @Target(AnnotationTarget.TYPE, AnnotationTarget.PROPERTY)
        @IntDef(
            NONE,
            INIT,
            CLEARED
        )
        annotation class State
    }

    @State
    internal var state: Int = NONE

    private val onInitHooks = HashSet<(() -> Unit)>()
    private val onDestroyHooks = HashSet<(() -> Unit)>()

    fun onInit(r: (() -> Unit)) {
        onInitHooks.add(r)
        if (state == INIT) {
            r()
        }
    }

    fun onCleared(r: (() -> Unit)) {
        onDestroyHooks.add(r)
        if (state == CLEARED) {
            r()
        }
    }

    fun dispatchOnInit() {
        state = INIT
        onInitHooks.forEach { it() }
    }

    fun dispatchOnCleared() {
        state = CLEARED
        onDestroyHooks.forEach { it() }
    }
}
