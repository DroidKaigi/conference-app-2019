package io.github.droidkaigi.confsched2019.staff.ui.actioncreator

import androidx.lifecycle.Lifecycle
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.data.repository.StaffRepository
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.dummyStaffContentsData
import io.github.droidkaigi.confsched2019.ext.android.CoroutinePlugin
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.StaffSearchResult
import io.github.droidkaigi.confsched2019.widget.component.TestLifecycleOwner
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class StaffSearchActionCreatorTest {
    @RelaxedMockK lateinit var dispatcher: Dispatcher
    @RelaxedMockK lateinit var staffRepository: StaffRepository

    private val lifecycleOwner = TestLifecycleOwner().handleEvent(Lifecycle.Event.ON_RESUME)
    private lateinit var target: StaffSearchActionCreator

    @Before fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        CoroutinePlugin.mainDispatcherHandler = { Dispatchers.Default }

        target = StaffSearchActionCreator(
            dispatcher = dispatcher,
            staffRepository = staffRepository,
            lifecycle = lifecycleOwner.lifecycle
        )
    }

    @Test fun load() = runBlocking {
        coEvery { staffRepository.staffContents() } returns dummyStaffContentsData()

        target.load()

        coVerify {
            dispatcher.dispatch(Action.StaffSearchLoadingStateChanged(LoadingState.LOADING))
            dispatcher.dispatch(Action.StaffLoaded(dummyStaffContentsData()))
            staffRepository.refresh()
            dispatcher.dispatch(Action.StaffLoaded(dummyStaffContentsData()))
            dispatcher.dispatch(Action.StaffSearchLoadingStateChanged(LoadingState.LOADED))
        }
    }

    @Test fun testEmptySearch() {
        val query = ""
        val dummyStaffContentsData = dummyStaffContentsData()

        target.search(query, dummyStaffContentsData)

        verify {
            dispatcher.launchAndDispatch(
                Action.StaffSearchResultLoaded(
                    StaffSearchResult(
                        dummyStaffContentsData.staffs,
                        query
                    )
                )
            )
        }
    }

    @Test fun testNonEmptySearch() {
        val dummyStaffContentsData = dummyStaffContentsData()
        val query = "name1"

        target.search(query, dummyStaffContentsData)

        verify {
            dummyStaffContentsData.search(query)
            dispatcher.launchAndDispatch(
                Action.StaffSearchResultLoaded(
                    dummyStaffContentsData.search(
                        query
                    )
                )
            )
        }
    }
}
