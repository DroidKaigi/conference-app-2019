package io.github.droidkaigi.confsched2019.staff_dfm.ui.di

import dagger.Component
import io.github.droidkaigi.confsched2019.di.AppComponent
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.staff_dfm.ui.StaffSearchFragment

@PageScope
@Component(
    modules = [
        StaffModule::class
    ],
    dependencies = [AppComponent::class]
)
interface StaffComponent {
    @Component.Builder
    interface Builder {
        fun build(): StaffComponent
        fun appComponent(appComponent: AppComponent): Builder
        fun staffModule(staffModule: StaffModule): Builder
    }

    fun inject(fragment: StaffSearchFragment)
}
