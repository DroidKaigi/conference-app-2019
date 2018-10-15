package io.github.droidkaigi.confsched2019.session.ui.store

import androidx.lifecycle.ViewModel
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import javax.inject.Inject

class AllSessionsStore @Inject constructor(
    dispatcher: Dispatcher
) : ViewModel()
