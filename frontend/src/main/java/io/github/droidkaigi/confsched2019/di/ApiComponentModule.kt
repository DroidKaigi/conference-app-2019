package io.github.droidkaigi.confsched2019.di

import android.app.Application
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.data.api.ApiComponent
import io.github.droidkaigi.confsched2019.data.api.SessionApi
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.IO
import javax.inject.Singleton

@Module
object ApiComponentModule {
    @JvmStatic
    @Provides
    @Singleton
    fun provideApi(application: Application): SessionApi {
        return ApiComponent.builder()
                .context(application)
                .coroutineContext(Dispatchers.IO)
                .build()
                .sessionApi()
    }
}
