package io.github.droidkaigi.confsched2019.sponsor.ui.actioncreator

import androidx.lifecycle.Lifecycle
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.data.repository.SponsorRepository
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.dummySponsorCategoriesData
import io.github.droidkaigi.confsched2019.ext.android.CoroutinePlugin
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.widget.component.TestLifecycleOwner
import io.mockk.MockKAnnotations
import io.mockk.Ordering
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class SponsorActionCreatorTest {
    @RelaxedMockK lateinit var dispatcher: Dispatcher
    @RelaxedMockK lateinit var sponsorRepository: SponsorRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        CoroutinePlugin.mainDispatcherHandler = { Dispatchers.Default }
    }

    @Test
    fun load() = runBlocking {
        val lifecycleOwner = TestLifecycleOwner().handleEvent(Lifecycle.Event.ON_RESUME)
        coEvery { sponsorRepository.sponsors() } returns dummySponsorCategoriesData()
        val sponsorActionCreator = SponsorActionCreator(
            dispatcher,
            sponsorRepository,
            lifecycleOwner.lifecycle
        )

        sponsorActionCreator.load()

        coVerify(ordering = Ordering.ORDERED) {
            dispatcher.dispatch(Action.SponsorLoadingStateChanged(LoadingState.LOADING))
            sponsorRepository.sponsors()
            dispatcher.dispatch(Action.SponsorLoaded(dummySponsorCategoriesData()))
            sponsorRepository.refresh()
            sponsorRepository.sponsors()
            dispatcher.dispatch(Action.SponsorLoaded(dummySponsorCategoriesData()))
            dispatcher.dispatch(Action.SponsorLoadingStateChanged(LoadingState.LOADED))
        }
    }
}
