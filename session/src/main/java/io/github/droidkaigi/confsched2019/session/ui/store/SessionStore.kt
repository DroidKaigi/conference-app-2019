package io.github.droidkaigi.confsched2019.session.ui.store

import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionStore @Inject constructor(
    private val dispatcher: Dispatcher
) {
}
