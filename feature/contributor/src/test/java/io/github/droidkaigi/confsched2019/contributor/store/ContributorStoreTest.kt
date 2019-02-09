package io.github.droidkaigi.confsched2019.contributor.store

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.dummyContributorsData
import io.github.droidkaigi.confsched2019.ext.CoroutinePlugin
import io.github.droidkaigi.confsched2019.ext.changedForever
import io.github.droidkaigi.confsched2019.model.ContributorContents
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.widget.component.DirectDispatcher
import io.mockk.MockKAnnotations
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ContributorStoreTest {
    @JvmField @Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        CoroutinePlugin.mainDispatcherHandler = { DirectDispatcher }
    }

    @Test fun loadingState() = runBlocking {
        val dispatcher = Dispatcher()
        val contributorStore = ContributorStore(dispatcher)
        val observer = mockk<(LoadingState?) -> Unit>(relaxed = true)

        contributorStore.loadingState.changedForever(observer)
        verify { observer(LoadingState.LOADING) }

        dispatcher.dispatch(Action.ContributorLoadingStateChanged(LoadingState.LOADED))
        verify { observer(LoadingState.LOADED) }
    }

    @Test fun contributors() = runBlocking {
        val dispatcher = Dispatcher()
        val contributorStore = ContributorStore(dispatcher)
        val observer: (ContributorContents) -> Unit = mockk(relaxed = true)
        contributorStore.contributors.changedForever(observer)
        verify { observer(ContributorContents(emptyList())) }

        val dummyContributors = dummyContributorsData()
        dispatcher.dispatch(
            Action.ContributorLoaded(dummyContributors)
        )
        verify { observer(dummyContributors) }
    }
}
