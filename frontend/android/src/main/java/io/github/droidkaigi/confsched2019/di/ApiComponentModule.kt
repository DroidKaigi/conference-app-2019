package io.github.droidkaigi.confsched2019.di

import android.app.Application
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.data.api.ApiComponent
import io.github.droidkaigi.confsched2019.data.api.SessionApi
import io.github.droidkaigi.confsched2019.data.api.SponsorApi
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module object ApiComponentModule {
    @JvmStatic @Provides @Singleton fun provideApi(application: Application): SessionApi {
        return ApiComponent.builder()
            .context(application)
            .coroutineContext(Dispatchers.IO)
            .build()
            .sessionApi()
    }

    @JvmStatic @Provides @Singleton fun provideSponsorApi(application: Application): SponsorApi {
        return ApiComponent.builder()
            .context(application)
            .coroutineContext(Dispatchers.IO)
            .build()
            .sponsorApi()
    }
}
