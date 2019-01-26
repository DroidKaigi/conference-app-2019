package io.github.droidkaigi.confsched2019.staff_dfm.ui.di

import androidx.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.staff_dfm.ui.StaffSearchFragment

@Module
class StaffModule(private val fragment: StaffSearchFragment) {

    @Provides
    @PageScope
    fun providesLifecycle(): Lifecycle {
        return fragment.viewLifecycleOwner.lifecycle
    }
}
