package io.github.droidkaigi.confsched2019.data.repository

import io.github.droidkaigi.confsched2019.model.WifiConfiguration

interface WifiConfigurationRepository {
    suspend fun save(newConfiguration: WifiConfiguration): Boolean
}
