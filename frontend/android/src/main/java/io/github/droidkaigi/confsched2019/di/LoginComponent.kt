package io.github.droidkaigi.confsched2019.di

import dagger.Subcomponent
import io.github.droidkaigi.confsched2019.ui.MainActivity

@PageScope
@Subcomponent(modules = [ScreenModule::class])
interface ScreenComponent {
}
