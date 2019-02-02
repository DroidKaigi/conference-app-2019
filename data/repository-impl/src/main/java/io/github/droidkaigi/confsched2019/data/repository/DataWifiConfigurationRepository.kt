package io.github.droidkaigi.confsched2019.data.repository

import io.github.droidkaigi.confsched2019.data.device.WifiManager
import io.github.droidkaigi.confsched2019.model.WifiConfiguration
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class DataWifiConfigurationRepository @Inject constructor(
    private val wifiManager: WifiManager
) : WifiConfigurationRepository {
    override suspend fun save(newConfiguration: WifiConfiguration): Boolean = coroutineScope {
        try {
            wifiManager.createWifiConfiguration(newConfiguration)
            true
        } catch (ignored: Throwable) {
            false
        }
    }
}
