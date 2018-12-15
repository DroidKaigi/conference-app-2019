package io.github.droidkaigi.confsched2019.data.api

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    ApiModule::class
])
interface ApiComponent {
    fun DroidKaigiApi(): DroidKaigiApi

    @Component.Builder
    interface Builder {
        @BindsInstance fun context(context: Context): Builder

        fun build(): ApiComponent
    }

    companion object {
        fun builder(): Builder = DaggerApiComponent.builder()
    }
}
