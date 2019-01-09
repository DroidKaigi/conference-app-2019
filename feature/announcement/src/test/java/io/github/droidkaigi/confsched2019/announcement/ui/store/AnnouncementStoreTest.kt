package io.github.droidkaigi.confsched2019.announcement.ui.store

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.dummyAnnouncementsData
import io.github.droidkaigi.confsched2019.ext.android.CoroutinePlugin
import io.github.droidkaigi.confsched2019.ext.android.changedForever
import io.github.droidkaigi.confsched2019.model.Announcement
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.mockk.MockKAnnotations
import io.mockk.mockk
import io.mockk.verifySequence
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AnnouncementStoreTest {
    @JvmField @Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        CoroutinePlugin.mainDispatcherHandler = { Dispatchers.Default }
    }

    @Test fun loadingState() = runBlocking {
        val dispatcher = Dispatcher()
        val announcementStore = AnnouncementStore(dispatcher)
        val observer = mockk<(LoadingState?) -> Unit>(relaxed = true)

        announcementStore.loadingState.changedForever(observer)

        dispatcher.dispatch(Action.AnnouncementLoadingStateChanged(LoadingState.LOADING))
        dispatcher.dispatch(Action.AnnouncementLoadingStateChanged(LoadingState.LOADED))

        verifySequence {
            observer(LoadingState.LOADING)
            observer(LoadingState.LOADED)
        }
    }

    @Test fun announcements() = runBlocking {
        val dispatcher = Dispatcher()
        val announcementStore = AnnouncementStore(dispatcher)
        val observer: (List<Announcement>) -> Unit = mockk(relaxed = true)
        announcementStore.announcements.changedForever(observer)
        val dummyAnnouncements = dummyAnnouncementsData()

        dispatcher.dispatch(
            Action.AnnouncementLoaded(dummyAnnouncements)
        )

        verifySequence {
            observer(emptyList())
            observer(dummyAnnouncements)
        }
    }
}
