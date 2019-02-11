package io.github.droidkaigi.confsched2019.staff_dfm.ui.di

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.di.PageScope

@Module
class PageModule(val lifecycleOwner: LifecycleOwner) {

    @Provides
    @PageScope
    fun providesLifecycle(): Lifecycle {
        return lifecycleOwner.lifecycle
    }
}
