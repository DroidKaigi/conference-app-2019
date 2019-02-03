package io.github.droidkaigi.confsched2019.di

import android.app.Application
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.data.device.DeviceComponent
import io.github.droidkaigi.confsched2019.data.device.WifiManager
import javax.inject.Singleton

@Module
object DeviceComponentModule {
    @JvmStatic @Provides @Singleton
    fun provideWifiManager(application: Application): WifiManager {
        return DeviceComponent.builder()
            .context(application)
            .build()
            .WifiManager()
    }
}
