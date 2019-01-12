package io.github.droidkaigi.confsched2019.announcement.ui.store

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.dummyAnnouncementsData
import io.github.droidkaigi.confsched2019.ext.android.CoroutinePlugin
import io.github.droidkaigi.confsched2019.ext.android.changedForever
import io.github.droidkaigi.confsched2019.model.Announcement
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.widget.component.DirectDispatcher
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AnnouncementStoreTest {
    @JvmField @Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        CoroutinePlugin.mainDispatcherHandler = { DirectDispatcher }
    }

    @Test fun loadingState() = runBlocking {
        val dispatcher = Dispatcher()
        val announcementStore = AnnouncementStore(dispatcher)
        val observer = mockk<(LoadingState?) -> Unit>(relaxed = true)

        announcementStore.loadingState.changedForever(observer)
        coVerify { observer(LoadingState.INITIALIZED) }

        dispatcher.dispatch(Action.AnnouncementLoadingStateChanged(LoadingState.LOADING))
        coVerify { observer(LoadingState.LOADING) }

        dispatcher.dispatch(Action.AnnouncementLoadingStateChanged(LoadingState.LOADED))
        coVerify { observer(LoadingState.LOADED) }
    }

    @Test fun announcements() = runBlocking {
        val dispatcher = Dispatcher()
        val announcementStore = AnnouncementStore(dispatcher)
        val observer: (List<Announcement>) -> Unit = mockk(relaxed = true)
        announcementStore.announcements.changedForever(observer)
        coVerify { observer(emptyList()) }

        val dummyAnnouncements = dummyAnnouncementsData()
        dispatcher.dispatch(
            Action.AnnouncementLoaded(dummyAnnouncements)
        )
        coVerify { observer(dummyAnnouncements) }
    }
}
