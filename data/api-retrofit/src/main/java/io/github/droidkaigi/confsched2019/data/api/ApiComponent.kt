package io.github.droidkaigi.confsched2019.data.api

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
@Component(modules = [
    ApiModule::class
])
interface ApiComponent {
    fun sessionApi(): SessionApi
    fun sponsorApi(): SponsorApi

    @Component.Builder
    interface Builder {
        @BindsInstance fun context(context: Context): Builder

        @BindsInstance fun coroutineContext(coroutineContext: CoroutineContext): Builder

        fun build(): ApiComponent
    }

    companion object {
        fun builder(): Builder = DaggerApiComponent.builder()
    }
}
