package io.github.droidkaigi.confsched2019.session.ui.actioncreator

import io.github.droidkaigi.confsched2019.data.repository.SessionRepository
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class SessionActionCreator @Inject constructor(
    val dispatcher: Dispatcher,
    val sessionRepository: SessionRepository
) : CoroutineScope by GlobalScope {

    fun load() = launch {
        try {
            sessionRepository.refresh()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

