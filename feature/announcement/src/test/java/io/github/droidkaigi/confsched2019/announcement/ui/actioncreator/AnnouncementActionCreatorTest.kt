package io.github.droidkaigi.confsched2019.announcement.ui.actioncreator

import androidx.lifecycle.Lifecycle
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.data.firestore.FireStore
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.dummyAnnouncementsData
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

class AnnouncementActionCreatorTest {
    @RelaxedMockK lateinit var dispatcher: Dispatcher
    @RelaxedMockK lateinit var fireStore: FireStore

    @Before fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        CoroutinePlugin.mainDispatcherHandler = { Dispatchers.Default }
    }

    @Test fun load() = runBlocking {
        val lifecycleOwner = TestLifecycleOwner().handleEvent(Lifecycle.Event.ON_RESUME)
        coEvery { fireStore.getAnnouncements() } returns dummyAnnouncementsData()
        val announcementActionCreator = AnnouncementActionCreator(
            dispatcher,
            fireStore,
            lifecycleOwner.lifecycle
        )

        announcementActionCreator.load()

        coVerify(ordering = Ordering.SEQUENCE) {
            dispatcher.dispatch(Action.AnnouncementLoadingStateChanged(LoadingState.LOADING))
            fireStore.getAnnouncements()
            dispatcher.dispatch(Action.AnnouncementLoaded(dummyAnnouncementsData()))
            dispatcher.dispatch(Action.AnnouncementLoadingStateChanged(LoadingState.LOADED))
        }
    }
}
