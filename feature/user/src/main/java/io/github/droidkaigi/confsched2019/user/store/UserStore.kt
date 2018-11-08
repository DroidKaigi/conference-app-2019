package io.github.droidkaigi.confsched2019.user.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.toLiveData
import io.github.droidkaigi.confsched2019.action.Action
import kotlinx.coroutines.channels.map
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
