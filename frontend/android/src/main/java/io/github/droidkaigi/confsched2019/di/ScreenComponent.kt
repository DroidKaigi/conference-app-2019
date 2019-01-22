package io.github.droidkaigi.confsched2019.di

import android.app.Activity
import dagger.Subcomponent
import dagger.android.DispatchingAndroidInjector
import io.github.droidkaigi.confsched2019.ui.MainActivityModule

@PageScope
@Subcomponent(modules = [
    MainActivityModule.MainActivityBuilder::class,
    ScreenModule::class
])
interface ScreenComponent {
    val activityInjector: DispatchingAndroidInjector<Activity>
}
