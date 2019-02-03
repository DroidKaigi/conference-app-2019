package io.github.droidkaigi.confsched2019.system.actioncreator

import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.data.device.WifiManager
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.model.Message
import io.github.droidkaigi.confsched2019.model.SystemProperty
import io.github.droidkaigi.confsched2019.model.WifiConfiguration
import io.github.droidkaigi.confsched2019.model.defaultLang
import io.github.droidkaigi.confsched2019.system.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import timber.log.Timber
import timber.log.error
import javax.inject.Inject

class SystemActionCreator @Inject constructor(
    private val dispatcher: Dispatcher,
    private val wifiManager: WifiManager
) : CoroutineScope by GlobalScope + SupervisorJob() {

    fun setupSystem() {
        val lang = defaultLang()
        val systemProperty = SystemProperty(lang)
        dispatcher.launchAndDispatch(Action.SystemPropertyLoaded(systemProperty))
    }

    fun registerWifiConfiguration(ssid: String, password: String) {
        launch {
            val newConfiguration = WifiConfiguration(ssid, password)

            try {
                wifiManager.createWifiConfiguration(newConfiguration)

                dispatcher.dispatch(
                    Action.WifiConfigurationChange(
                        newConfiguration.copy(
                            isRegistered = true
                        )
                    )
                )
                dispatcher.dispatch(
                    Action.ShowProcessingMessage(
                        Message.of(R.string.system_wifi_registered_message)
                    )
                )
            } catch (th: Throwable) {
                Timber.error(th) {
                    "while registering wifi configuration"
                }
                dispatcher.dispatch(
                    Action.WifiConfigurationChange(
                        newConfiguration.copy(
                            isRegistered = false
                        )
                    )
                )
            }
        }
    }

    fun allowRegisterWifiConfiguration() {
        // Erase the livedata cache
        dispatcher.launchAndDispatch(Action.WifiConfigurationChange(null))
    }

    fun showSystemMessage(message: Message) {
        dispatcher.launchAndDispatch(Action.ShowProcessingMessage(message))
    }
}
