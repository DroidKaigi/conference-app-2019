package io.github.droidkaigi.confsched2019.user.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import confsched2018.droidkaigi.github.io.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.toLiveData
import io.github.droidkaigi.confsched2019.model.Action
import kotlinx.coroutines.experimental.channels.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserStore @Inject constructor(
    dispatcher: Dispatcher
) : ViewModel() {
    val logined: LiveData<Boolean> = dispatcher.subscribe<Action.UserRegistered>()
        .map { true }
        .toLiveData(false)
}
