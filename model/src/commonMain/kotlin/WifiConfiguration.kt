package io.github.droidkaigi.confsched2019.model

data class WifiConfiguration(
    val ssid: String,
    val password: String,
    val isRegistered: Boolean = false
)
