package io.github.droidkaigi.confsched2019.widget

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.droidkaigi.confsched2019.di.PageComponent

class PageViewModel : ViewModel(), LifecycleOwner {
    lateinit var pageComponent: PageComponent

    private val lifecycle = LifecycleRegistry(this).apply {
        handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun getLifecycle(): Lifecycle = lifecycle

    override fun onCleared() {
        super.onCleared()
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }

    class Factory(val pageComponentCreator: (LifecycleOwner) -> PageComponent) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            val pageViewModel = PageViewModel()
            pageViewModel.pageComponent = pageComponentCreator(pageViewModel)
            @Suppress("UNCHECKED_CAST")
            return pageViewModel as T
        }
    }
}
