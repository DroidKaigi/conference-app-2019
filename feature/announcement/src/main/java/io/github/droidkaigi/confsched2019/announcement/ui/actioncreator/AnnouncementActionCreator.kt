package io.github.droidkaigi.confsched2019.announcement.ui.actioncreator

import androidx.lifecycle.Lifecycle
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.data.repository.AnnouncementRepository
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.coroutineScope
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.system.actioncreator.ErrorHandler
import io.github.droidkaigi.confsched2019.util.ScreenLifecycle
import io.github.droidkaigi.confsched2019.util.coroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class AnnouncementActionCreator @Inject constructor(
    override val dispatcher: Dispatcher,
    private val announcementRepository: AnnouncementRepository,
    private val screenLifecycle: ScreenLifecycle
) : CoroutineScope by screenLifecycle.coroutineScope,
    ErrorHandler {

    fun load() = launch {
        try {
            dispatcher.dispatch(Action.AnnouncementLoadingStateChanged(LoadingState.LOADING))
            // read the cache first
            dispatcher.dispatch(Action.AnnouncementLoaded(announcementRepository.announcements()))

            // use api response
            announcementRepository.refresh()
            dispatcher.dispatch(Action.AnnouncementLoaded(announcementRepository.announcements()))

            dispatcher.dispatch(Action.AnnouncementLoadingStateChanged(LoadingState.LOADED))
        } catch (e: Exception) {
            onError(e)
            dispatcher.dispatch(Action.AnnouncementLoadingStateChanged(LoadingState.INITIALIZED))
        }
    }
}
