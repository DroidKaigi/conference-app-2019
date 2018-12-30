package io.github.droidkaigi.confsched2019.session.ui.actioncreator

import androidx.lifecycle.Lifecycle
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.coroutineScope
import io.github.droidkaigi.confsched2019.model.SessionPage
import io.github.droidkaigi.confsched2019.system.actioncreator.ErrorHandler
import io.github.droidkaigi.confsched2019.widget.BottomSheetBehavior
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@PageScope
class SessionPageActionCreator @Inject constructor(
    override val dispatcher: Dispatcher,
    @PageScope private val lifecycle: Lifecycle
) : CoroutineScope by lifecycle.coroutineScope,
    ErrorHandler {
    fun toggleFilterExpanded(page: SessionPage) {
        dispatcher.launchAndDispatch(Action.BottomSheetFilterToggled(page))
    }

    fun changeFilterSheet(
        page: SessionPage,
        @BottomSheetBehavior.Companion.State bottomSheetState: Int
    ) {
        dispatcher.launchAndDispatch(Action.BottomSheetFilterStateChanged(page, bottomSheetState))
    }
}
