package io.github.droidkaigi.confsched2019.data.device

import android.content.Context
import androidx.core.content.ContextCompat
import io.github.droidkaigi.confsched2019.model.WifiConfiguration
import timber.log.Timber
import timber.log.debug
import javax.inject.Inject
import android.net.wifi.WifiConfiguration as SdkWifiConfiguration
import android.net.wifi.WifiManager as SdkWifiManager

class AndroidWifiManager @Inject constructor(
    context: Context
) : WifiManager {
    private val wifiManager: SdkWifiManager? =
        ContextCompat.getSystemService(context, SdkWifiManager::class.java)

    override fun createWifiConfiguration(wifiConfiguration: WifiConfiguration) {
        wifiManager ?: let {
            Timber.debug { "WiFi manager is not available" }
            throw WifiManager.NotFoundException()
        }

        Timber.debug {
            "SSID/PW = ${wifiConfiguration.ssid}/${wifiConfiguration.password}"
        }

        if (!wifiManager.isWifiEnabled) {
            Timber.debug { "WiFi manager was enabled" }
            wifiManager.isWifiEnabled = true
        }

        val configuration = wifiConfiguration.asSdk()

        val netId = wifiManager.addNetwork(configuration)

        Timber.debug { "Given network id is $netId" }

        if (netId != -1) {
            if (!wifiManager.enableNetwork(netId, false)) {
                throw WifiManager.CannotCreateException("could not create a configuration")
            }
        } else {
            throw WifiManager.CannotCreateException("could not add to network")
        }
    }

    private fun WifiConfiguration.asSdk(): SdkWifiConfiguration {
        return SdkWifiConfiguration().apply {
            SSID = ssid.quote()
            preSharedKey = password.quote()
        }
    }

    private fun String.quote(): String {
        return "\"${this}\""
    }
}
