package io.github.droidkaigi.confsched2019.data.device

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module(includes = [DeviceModule.Providers::class])
internal abstract class DeviceModule {
    @Binds abstract fun WifiManager(impl: AndroidWifiManager): WifiManager

    @Module
    internal object Providers {
        @JvmStatic @Provides fun wifiManager(context: Context): AndroidWifiManager {
            return AndroidWifiManager(context)
        }
    }
}
