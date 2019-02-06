package io.github.droidkaigi.confsched2019.contributor.actioncreator

import androidx.lifecycle.Lifecycle
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.data.repository.ContributorRepository
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.coroutineScope
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.system.actioncreator.ErrorHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class ContributorActionCreator @Inject constructor(
    override val dispatcher: Dispatcher,
    private val contributorRepository: ContributorRepository,
    @PageScope private val lifecycle: Lifecycle
) : CoroutineScope by lifecycle.coroutineScope, ErrorHandler {
    fun load() = launch {
        try {
            // Load Local cache
            dispatcher.dispatch(Action.ContributorLoadingStateChanged(LoadingState.LOADING))
            dispatcher.dispatch(
                Action.ContributorLoaded(contributorRepository.contributorContents())
            )
            // Load From API
            contributorRepository.refresh()
            dispatcher.dispatch(
                Action.ContributorLoaded(contributorRepository.contributorContents())
            )
            dispatcher.dispatch(Action.ContributorLoadingStateChanged(LoadingState.LOADED))
        } catch (e: Exception) {
            onError(e)
            dispatcher.dispatch(Action.ContributorLoadingStateChanged(LoadingState.INITIALIZED))
        }
    }
}
