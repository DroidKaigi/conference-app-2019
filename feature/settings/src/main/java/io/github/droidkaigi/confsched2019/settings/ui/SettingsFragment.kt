package io.github.droidkaigi.confsched2019.settings.ui

import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceFragmentCompat
import io.github.droidkaigi.confsched2019.settings.R
import timber.log.Timber
import timber.log.debug

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        requireNotNull(findPreference(getString(R.string.pref_key_add_wifi)))
            .setOnPreferenceClickListener {
                true.also {
                    registerWifi()
                }
            }
    }

    private fun registerWifi(): Boolean {
        val wifiManager = ContextCompat.getSystemService(requireContext(), WifiManager::class.java)
            ?: return false

        val configuration = WifiConfiguration().apply {
            SSID = getString(R.string.wifi_ssid).quote()
            preSharedKey = getString(R.string.wifi_password).quote()
        }

        Timber.debug { "Wifi Configuration : SSID/PW = ${configuration.SSID} / ${configuration.preSharedKey}" }

        if (!wifiManager.isWifiEnabled) {
            Timber.debug { "WiFi manager was enabled" }
            wifiManager.isWifiEnabled = true
        }

        val netId = wifiManager.addNetwork(configuration)

        Timber.debug { "Given network id is $netId" }

        return if (netId != -1) {
            wifiManager.enableNetwork(netId, false)
        } else {
            false
        }.also { result ->
            Timber.debug { "Attempt state is $result" }
        }
    }

    private fun String.quote(): String {
        return "\"${this}\""
    }
}
