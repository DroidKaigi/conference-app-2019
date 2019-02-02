package io.github.droidkaigi.confsched2019.sponsor.ui.store

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.dummySponsorsData
import io.github.droidkaigi.confsched2019.ext.android.CoroutinePlugin
import io.github.droidkaigi.confsched2019.ext.android.changedForever
import io.github.droidkaigi.confsched2019.model.SponsorCategory
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.widget.component.DirectDispatcher
import io.mockk.MockKAnnotations
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SponsorStoreTest {

    @JvmField @Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        CoroutinePlugin.mainDispatcherHandler = { DirectDispatcher }
    }

    @Test fun loadingState() = runBlocking {
        val dispatcher = Dispatcher()
        val sponsorStore = SponsorStore(dispatcher)
        val observer = mockk<(LoadingState?) -> Unit>(relaxed = true)

        sponsorStore.loadingState.changedForever(observer)
        verify { observer(LoadingState.LOADING) }

        dispatcher.dispatch(Action.SponsorLoadingStateChanged(LoadingState.LOADED))
        verify { observer(LoadingState.LOADED) }
    }

    @Test fun sponsors() = runBlocking {
        val dispatcher = Dispatcher()
        val sponsorStore = SponsorStore(dispatcher)
        val observer: (List<SponsorCategory>) -> Unit = mockk(relaxed = true)
        sponsorStore.sponsors.changedForever(observer)
        verify { observer(emptyList()) }

        val dummySponsors = dummySponsorsData()
        dispatcher.dispatch(
            Action.SponsorLoaded(dummySponsors)
        )
        verify { observer(dummySponsors) }
    }
}
