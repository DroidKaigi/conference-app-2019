package io.github.droidkaigi.confsched2019.di

import android.app.Application
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.data.api.ApiComponent
import io.github.droidkaigi.confsched2019.data.api.DroidKaigiApi
import javax.inject.Singleton

@Module
object ApiComponentModule {
    @JvmStatic @Provides @Singleton fun provideApi(application: Application): DroidKaigiApi {
        return ApiComponent.builder()
            .context(application)
            .build()
            .DroidKaigiApi()
    }
}
