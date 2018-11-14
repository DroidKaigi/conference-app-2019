package io.github.droidkaigi.confsched2019.system.store

import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.toLiveData
import io.github.droidkaigi.confsched2019.ext.android.toSingleLiveData
import io.github.droidkaigi.confsched2019.model.Lang
import io.github.droidkaigi.confsched2019.model.SystemProperty
import kotlinx.coroutines.channels.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SystemStore @Inject constructor(
    dispatcher: Dispatcher
) {
    val errorMsg = dispatcher
        .subscribe<Action.Error>()
        .map { it.msg }
        .toSingleLiveData(null)

    val systemProperty = dispatcher
        .subscribe<Action.SystemPropertyLoaded>()
        .map { it.system }
        .toLiveData(SystemProperty(Lang.EN))
    val lang
        get() = systemProperty.value!!.lang
}
