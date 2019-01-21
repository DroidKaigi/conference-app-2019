package io.github.droidkaigi.confsched2019.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import io.github.droidkaigi.confsched2019.App
import io.github.droidkaigi.confsched2019.ui.MainActivityModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        MainActivityModule.MainActivityBuilder::class,
        DbComponentModule::class,
        RepositoryComponentModule::class,
        FirestoreComponentModule::class,
        ApiComponentModule::class
    ]
)
interface AppComponent : AndroidInjector<App> {
    @Component.Builder
    interface Builder {
        @BindsInstance fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun plus(screenModule: ScreenModule): ScreenComponent

    override fun inject(app: App)
}

fun Application.createAppComponent() = DaggerAppComponent.builder()
    .application(this)
    .build()
