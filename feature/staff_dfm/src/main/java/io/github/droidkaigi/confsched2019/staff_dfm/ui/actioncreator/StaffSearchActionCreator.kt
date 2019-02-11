package io.github.droidkaigi.confsched2019.staff_dfm.ui.actioncreator

import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.data.repository.StaffRepository
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.StaffContents
import io.github.droidkaigi.confsched2019.model.StaffSearchResult
import io.github.droidkaigi.confsched2019.system.actioncreator.ErrorHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@PageScope
class StaffSearchActionCreator @Inject constructor(
    override val dispatcher: Dispatcher,
    private val staffRepository: StaffRepository,
    @PageScope private val coroutineScope: CoroutineScope
) : CoroutineScope by coroutineScope, ErrorHandler {

    fun load() = launch {
        try {
            dispatcher.dispatch(Action.StaffSearchLoadingStateChanged(LoadingState.LOADING))
            // load cache data
            dispatcher.dispatch(Action.StaffLoaded(staffRepository.staffContents()))
            // fetch from api
            staffRepository.refresh()
            // load fetched data
            dispatcher.dispatch(Action.StaffLoaded(staffRepository.staffContents()))
            dispatcher.dispatch(Action.StaffSearchLoadingStateChanged(LoadingState.LOADED))
        } catch (e: Exception) {
            dispatcher.dispatch(Action.StaffSearchLoadingStateChanged(LoadingState.INITIALIZED))
        }
    }

    fun search(query: String?, staffContents: StaffContents) {
        // if we do not have query, we should show all staffs
        if (query.isNullOrBlank()) {
            dispatcher.launchAndDispatch(
                Action.StaffSearchResultLoaded(
                    StaffSearchResult(
                        staffContents.staffs,
                        query
                    )
                )
            )
            return
        }
        val searchResult = staffContents.search(query)
        dispatcher.launchAndDispatch(Action.StaffSearchResultLoaded(searchResult))
    }
}
