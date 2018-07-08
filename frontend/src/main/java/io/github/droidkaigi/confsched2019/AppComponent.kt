package io.github.droidkaigi.confsched2019

import android.app.Activity
import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.DispatchingAndroidInjector
import io.github.droidkaigi.confsched2019.ui.MainActivityModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
  MainActivityModule::class
])
interface AppComponent {
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