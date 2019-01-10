package io.github.droidkaigi.confsched2019.session.ui.store

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.dummySessionData
import io.github.droidkaigi.confsched2019.ext.android.CoroutinePlugin
import io.github.droidkaigi.confsched2019.ext.android.changedForever
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.SessionContents
import io.kotlintest.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.mockk
import io.mockk.verifySequence
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SessionContentsStoreTest {
    @JvmField @Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        CoroutinePlugin.mainDispatcherHandler = { Dispatchers.Default }
    }

    @Test fun loadingState() = runBlocking<Unit> {
        val dispatcher = Dispatcher()
        val sessionsStore = SessionContentsStore(dispatcher)
        val observer = mockk<(LoadingState?) -> Unit>(relaxed = true)

        sessionsStore.loadingState.changedForever(observer)

        dispatcher.dispatch(Action.SessionLoadingStateChanged(LoadingState.LOADING))
        dispatcher.dispatch(Action.SessionLoadingStateChanged(LoadingState.LOADED))

        sessionsStore.isLoading shouldBe false
        verifySequence {
            observer(LoadingState.INITIALIZED)
            observer(LoadingState.LOADING)
            observer(LoadingState.LOADED)
        }
    }

    @Test fun sessions() = runBlocking<Unit> {
        val dispatcher = Dispatcher()
        val sessionsStore = SessionContentsStore(dispatcher)
        val observer: (SessionContents) -> Unit = mockk(relaxed = true)
        sessionsStore.sessionContents.changedForever(observer)
        val dummySessionContents = SessionContents.EMPTY.copy(sessions = dummySessionData())

        dispatcher.dispatch(
            Action.SessionContentsLoaded(dummySessionContents)
        )

        verifySequence {
            observer(SessionContents.EMPTY)
            observer(dummySessionContents)
        }
    }
}
