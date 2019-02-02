package io.github.droidkaigi.confsched2019.data.device

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        DeviceModule::class
    ]
)
interface DeviceComponent {
    fun WifiManager(): WifiManager

    @Component.Builder
    interface Builder {
        @BindsInstance fun context(context: Context): Builder

        fun build(): DeviceComponent
    }

    companion object {
        fun builder(): Builder = DaggerDeviceComponent.builder()
    }
}
