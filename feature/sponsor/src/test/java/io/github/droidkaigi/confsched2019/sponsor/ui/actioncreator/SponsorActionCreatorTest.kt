package io.github.droidkaigi.confsched2019.sponsor.ui.actioncreator

import androidx.lifecycle.Lifecycle
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.data.repository.SponsorRepository
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.CoroutinePlugin
import io.github.droidkaigi.confsched2019.widget.component.TestLifecycleOwner
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class SponsorActionCreatorTest {
    @RelaxedMockK
    lateinit var dispatcher: Dispatcher
    @RelaxedMockK lateinit var repository: SponsorRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        CoroutinePlugin.mainDispatcherHandler = { Dispatchers.Default }
    }


    @Test
    fun openSponsorLink() = runBlocking {
        val lifecycleOwner = TestLifecycleOwner().handleEvent(Lifecycle.Event.ON_RESUME)
        val sponsorActionCreator = SponsorActionCreator(
            dispatcher,
            repository,
            lifecycleOwner.lifecycle
        )
        val sponsorUrl = "https://www.example.com"

        sponsorActionCreator.openSponsorLink(sponsorUrl)

        coVerify {
            dispatcher.dispatch(Action.SponsorOpenLink(sponsorUrl))
        }
    }

    @Test
    fun clearSponsorLink() {
        val lifecycleOwner = TestLifecycleOwner().handleEvent(Lifecycle.Event.ON_RESUME)
        val sponsorActionCreator = SponsorActionCreator(
            dispatcher,
            repository,
            lifecycleOwner.lifecycle
        )
        val sponsorUrl = "https://www.example.com"

        sponsorActionCreator.openSponsorLink(sponsorUrl)
        sponsorActionCreator.clearSponsorLink()

        coVerify {
            dispatcher.dispatch(Action.SponsorOpenLink(sponsorUrl))
            dispatcher.dispatch(Action.SponsorOpenLink(null))
        }
    }
}
