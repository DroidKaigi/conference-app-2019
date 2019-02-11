package io.github.droidkaigi.confsched2019.staff_dfm.ui.di

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProviders
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.staff_dfm.ui.StaffSearchFragment
import io.github.droidkaigi.confsched2019.widget.PageViewModel

@Module
class StaffModule(private val fragment: StaffSearchFragment) {

    @Provides
    @PageScope
    fun providesLifecycle(): Lifecycle {
        return ViewModelProviders
            .of(fragment)
            .get(PageViewModel::class.java)
            .getLifecycle()
    }
}
