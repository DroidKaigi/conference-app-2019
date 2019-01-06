package io.github.droidkaigi.confsched2019.di

import android.app.Application
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.data.api.ApiComponent
import io.github.droidkaigi.confsched2019.data.api.DroidKaigiApi
import io.github.droidkaigi.confsched2019.data.api.GoogleFormApi
import javax.inject.Singleton

@Module
object ApiComponentModule {
    @JvmStatic @Provides @Singleton
    fun provideDroidKaigiApi(application: Application): DroidKaigiApi {
        return ApiComponent.builder()
            .context(application)
            .build()
            .DroidKaigiApi()
    }

    @JvmStatic @Provides @Singleton
    fun provideGoogleFormApi(application: Application): GoogleFormApi {
        return ApiComponent.builder()
            .context(application)
            .build()
            .GoogleFormApi()
    }
}
