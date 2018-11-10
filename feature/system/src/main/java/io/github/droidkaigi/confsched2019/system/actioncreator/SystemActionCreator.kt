package io.github.droidkaigi.confsched2019.system.actioncreator

import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.model.Lang
import io.github.droidkaigi.confsched2019.model.SystemProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.plus
import java.util.Locale
import javax.inject.Inject

class SystemActionCreator @Inject constructor(
    val dispatcher: Dispatcher
) : CoroutineScope by GlobalScope + SupervisorJob() {

    fun setupSystem() {
        val lang = if (Locale.getDefault().equals(Locale.JAPAN)) Lang.JA else Lang.EN
        val systemProperty = SystemProperty(lang)
        dispatcher.launchAndSend(Action.SystemPropertyLoaded(systemProperty))
    }

    private fun onError(e: Exception? = null) {
        e?.printStackTrace()
    }
}
