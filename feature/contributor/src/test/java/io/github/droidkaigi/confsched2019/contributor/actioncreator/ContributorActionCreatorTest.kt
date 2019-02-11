package io.github.droidkaigi.confsched2019.contributor.actioncreator

import androidx.lifecycle.Lifecycle
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.data.repository.ContributorRepository
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.dummyContributorsData
import io.github.droidkaigi.confsched2019.ext.CoroutinePlugin
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

class ContributorActionCreatorTest {
    @RelaxedMockK lateinit var dispatcher: Dispatcher
    @RelaxedMockK lateinit var contributorRepository: ContributorRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        CoroutinePlugin.mainDispatcherHandler = { Dispatchers.Default }
    }

    @Test
    fun load() = runBlocking {
        val lifecycleOwner = TestLifecycleOwner().handleEvent(Lifecycle.Event.ON_RESUME)
        coEvery { contributorRepository.contributorContents() } returns dummyContributorsData()
        val contributorActionCreator = ContributorActionCreator(
            dispatcher,
            contributorRepository,
            lifecycleOwner.lifecycle
        )

        contributorActionCreator.load()

        coVerify(ordering = Ordering.ORDERED) {
            dispatcher.dispatch(Action.ContributorLoadingStateChanged(LoadingState.LOADING))
            contributorRepository.contributorContents()
            dispatcher.dispatch(Action.ContributorLoaded(dummyContributorsData()))
            contributorRepository.refresh()
            contributorRepository.contributorContents()
            dispatcher.dispatch(Action.ContributorLoaded(dummyContributorsData()))
            dispatcher.dispatch(Action.ContributorLoadingStateChanged(LoadingState.LOADED))
        }
    }
}
