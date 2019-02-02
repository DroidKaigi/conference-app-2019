package io.github.droidkaigi.confsched2019.system.actioncreator

import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.data.repository.WifiConfigurationRepository
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.model.SystemProperty
import io.github.droidkaigi.confsched2019.model.WifiConfiguration
import io.github.droidkaigi.confsched2019.model.defaultLang
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

class SystemActionCreator @Inject constructor(
    val dispatcher: Dispatcher,
    private val wifiConfigurationRepository: WifiConfigurationRepository
) : CoroutineScope by GlobalScope + SupervisorJob() {

    fun setupSystem() {
        val lang = defaultLang()
        val systemProperty = SystemProperty(lang)
        dispatcher.launchAndDispatch(Action.SystemPropertyLoaded(systemProperty))
    }

    fun registerWifiConfiguration(ssid: String, password: String) {
        launch {
            val registered = wifiConfigurationRepository.save(WifiConfiguration(ssid, password))

            dispatcher.dispatch(Action.WifiConfigurationRegistered(registered))
            dispatcher.dispatch(Action.WifiConfigurationRegistered(null))
        }
    }
}
