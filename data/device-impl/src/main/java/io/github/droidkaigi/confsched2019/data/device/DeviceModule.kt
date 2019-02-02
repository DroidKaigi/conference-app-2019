package io.github.droidkaigi.confsched2019.data.device

import dagger.Binds
import dagger.Module

@Module(includes = [DeviceModule.Providers::class])
internal abstract class DeviceModule {
    @Binds abstract fun WifiManager(impl: AndroidWifiManager): WifiManager

    @Module
    internal object Providers {
    }
}
