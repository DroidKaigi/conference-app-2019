package io.github.droidkaigi.confsched2019.user.store

import androidx.lifecycle.ViewModel
import confsched2018.droidkaigi.github.io.dispatcher.Dispatcher
import javax.inject.Inject

class UserStore @Inject constructor(
        dispatcher: Dispatcher
) : ViewModel() {
    // TODO delete it
//    val sessionsLiveData: LiveData<List<Session>> = dispatcher.subscrive<Action.AllSessionLoaded>()
//            .map { it.sessions }
//            .toLiveData()
}
