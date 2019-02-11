package io.github.droidkaigi.confsched2019.session.ui.store

import androidx.lifecycle.LiveData
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.toSingleLiveData
import io.github.droidkaigi.confsched2019.model.SessionPage
import io.github.droidkaigi.confsched2019.store.Store
import io.github.droidkaigi.confsched2019.widget.BottomSheetBehavior
import kotlinx.coroutines.channels.filter
import kotlinx.coroutines.channels.map

class SessionPageStore @AssistedInject constructor(
    dispatcher: Dispatcher,
    @Assisted val sessionPage: SessionPage
) : Store() {
    @AssistedInject.Factory
    interface Factory {
        fun create(sessionPage: SessionPage): SessionPageStore
    }

    val filterSheetState: LiveData<Int> = dispatcher
        .subscribe<Action.BottomSheetFilterStateChanged>()
        .filter { it.page == sessionPage }
        .map { it.bottomSheetState }
        .toSingleLiveData(this, BottomSheetBehavior.STATE_EXPANDED)

    val toggleFilterSheet: LiveData<Unit> = dispatcher
        .subscribe<Action.BottomSheetFilterToggled>()
        .filter { it.page == sessionPage }
        .map { Unit }
        .toSingleLiveData(this, Unit)
}
