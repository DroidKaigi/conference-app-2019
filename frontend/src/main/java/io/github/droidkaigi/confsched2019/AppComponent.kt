package io.github.droidkaigi.confsched2019

import android.app.Activity
import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import io.github.droidkaigi.confsched2019.ui.MainActivityModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    MainActivityModule::class,
    DbComponentModule::class,
    ApiComponentModule::class
])
interface AppComponent : AndroidInjector<App> {
    val activityInjector: DispatchingAndroidInjector<Activity>

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }
}

fun Application.createAppComponent() = DaggerAppComponent.builder()
        .application(this)
        .build()