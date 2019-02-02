package io.github.droidkaigi.confsched2019.staff.ui.store

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.dummySearchResultData
import io.github.droidkaigi.confsched2019.dummyStaffContentsData
import io.github.droidkaigi.confsched2019.ext.android.CoroutinePlugin
import io.github.droidkaigi.confsched2019.ext.android.changedForever
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.StaffContents
import io.github.droidkaigi.confsched2019.model.StaffSearchResult
import io.github.droidkaigi.confsched2019.widget.component.DirectDispatcher
import io.mockk.MockKAnnotations
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class StaffSearchStoreTest {
    @JvmField @Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        CoroutinePlugin.mainDispatcherHandler = { DirectDispatcher }
    }

    @Test fun loadState() = runBlocking {
        val dispatcher = Dispatcher()
        val staffSearchStore = StaffSearchStore(dispatcher)
        val observer = mockk<(LoadingState?) -> Unit>(relaxed = true)

        staffSearchStore.loadingState.changedForever(observer)
        verify { observer(LoadingState.LOADING) }

        dispatcher.dispatch(Action.StaffSearchLoadingStateChanged(LoadingState.LOADED))
        verify { observer(LoadingState.LOADED) }
    }

    @Test fun searchResult() = runBlocking {
        val dispatcher = Dispatcher()
        val staffSearchStore = StaffSearchStore(dispatcher)
        val observer: (StaffSearchResult) -> Unit = mockk(relaxed = true)
        staffSearchStore.searchResult.changedForever(observer)
        verify { observer(StaffSearchResult.EMPTY) }

        val dummyStaffSearchResult = dummySearchResultData()
        dispatcher.dispatch(
            Action.StaffSearchResultLoaded(dummyStaffSearchResult)
        )
        verify { observer(dummyStaffSearchResult) }
    }

    @Test fun staffContents() = runBlocking {
        val dispatcher = Dispatcher()
        val staffSearchStore = StaffSearchStore(dispatcher)
        val observer: (StaffContents) -> Unit = mockk(relaxed = true)
        staffSearchStore.staffContents.changedForever(observer)
        verify { observer(StaffContents.EMPTY) }

        val dummyStaffContents = dummyStaffContentsData()
        dispatcher.dispatch(
            Action.StaffLoaded(dummyStaffContents)
        )
        verify { observer(dummyStaffContents) }
    }
}
