package io.github.droidkaigi.confsched2019.staff_dfm.ui.di

import dagger.Subcomponent
import io.github.droidkaigi.confsched2019.staff_dfm.ui.StaffSearchFragment

@Subcomponent
interface StaffComponent {
    @Subcomponent.Builder
    interface Builder {
        fun build(): StaffComponent
    }

    fun inject(fragment: StaffSearchFragment)
}
