package io.github.droidkaigi.confsched2019.model

enum class LoadingState {
    INITIALIZED,
    LOADING,
    LOADED;

    val isInitialized: Boolean get() = this == LoadingState.INITIALIZED
    val isLoading get() = this == LoadingState.LOADING
    val isLoaded: Boolean get() = this == LoadingState.LOADED
}
