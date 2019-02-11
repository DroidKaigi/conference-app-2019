package io.github.droidkaigi.confsched2019.staff_dfm.ui.di

import dagger.Component
import io.github.droidkaigi.confsched2019.di.AppComponent
import io.github.droidkaigi.confsched2019.di.PageComponent
import io.github.droidkaigi.confsched2019.di.PageScope

@PageScope
@Component(
    modules = [
        PageModule::class
    ],
    dependencies = [AppComponent::class]
)
interface StaffPageComponent : PageComponent {
    @Component.Builder
    interface Builder {
        fun pageModule(pageModule: PageModule): Builder
        fun appComponent(appComponent: AppComponent): Builder
        fun build(): StaffPageComponent
    }

    fun staffComponentBuilder(): StaffComponent.Builder
}

