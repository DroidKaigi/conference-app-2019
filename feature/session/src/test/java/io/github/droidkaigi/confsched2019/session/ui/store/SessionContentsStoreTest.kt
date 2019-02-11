package io.github.droidkaigi.confsched2019.session.ui.store

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.dummySessionData
import io.github.droidkaigi.confsched2019.ext.CoroutinePlugin
import io.github.droidkaigi.confsched2019.ext.changedForever
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.SessionContents
import io.github.droidkaigi.confsched2019.widget.component.DirectDispatcher
import io.mockk.MockKAnnotations
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SessionContentsStoreTest {
    @JvmField @Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        CoroutinePlugin.mainDispatcherHandler = { DirectDispatcher }
    }

    @Test fun loadingState() = runBlocking<Unit> {
        val dispatcher = Dispatcher()
        val sessionsStore = SessionContentsStore(dispatcher)
        val observer = mockk<(LoadingState?) -> Unit>(relaxed = true)

        sessionsStore.loadingState.changedForever(observer)
        verify { observer(LoadingState.INITIALIZED) }

        dispatcher.dispatch(Action.SessionLoadingStateChanged(LoadingState.LOADING))
        verify { observer(LoadingState.LOADING) }
        dispatcher.dispatch(Action.SessionLoadingStateChanged(LoadingState.LOADED))
        verify { observer(LoadingState.LOADED) }
    }

    @Test fun sessions() = runBlocking<Unit> {
        val dispatcher = Dispatcher()
        val sessionsStore = SessionContentsStore(dispatcher)
        val observer: (SessionContents) -> Unit = mockk(relaxed = true)

        sessionsStore.sessionContents.changedForever(observer)
        verify { observer(SessionContents.EMPTY) }

        val dummySessionContents = SessionContents.EMPTY.copy(sessions = dummySessionData())
        dispatcher.dispatch(
            Action.SessionContentsLoaded(dummySessionContents)
        )
        verify { observer(dummySessionContents) }
    }
}
