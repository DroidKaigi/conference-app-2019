package io.github.droidkaigi.confsched2019.announcement.ui.actioncreator

import androidx.lifecycle.Lifecycle
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.data.repository.AnnouncementRepository
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.dummyAnnouncementsData
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

class AnnouncementActionCreatorTest {
    @RelaxedMockK lateinit var dispatcher: Dispatcher
    @RelaxedMockK lateinit var announcementRepository: AnnouncementRepository

    @Before fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        CoroutinePlugin.mainDispatcherHandler = { Dispatchers.Default }
    }

    @Test fun load() = runBlocking {
        val lifecycleOwner = TestLifecycleOwner().handleEvent(Lifecycle.Event.ON_RESUME)
        coEvery { announcementRepository.announcements() } returns dummyAnnouncementsData()
        val announcementActionCreator = AnnouncementActionCreator(
            dispatcher,
            announcementRepository,
            lifecycleOwner.lifecycle
        )

        announcementActionCreator.load()

        coVerify(ordering = Ordering.ORDERED) {
            dispatcher.dispatch(Action.AnnouncementLoadingStateChanged(LoadingState.LOADING))
            announcementRepository.announcements()
            dispatcher.dispatch(Action.AnnouncementLoaded(dummyAnnouncementsData()))
            announcementRepository.refresh()
            announcementRepository.announcements()
            dispatcher.dispatch(Action.AnnouncementLoaded(dummyAnnouncementsData()))
            dispatcher.dispatch(Action.AnnouncementLoadingStateChanged(LoadingState.LOADED))
        }
    }
}
