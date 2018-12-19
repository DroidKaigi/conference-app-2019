package io.github.droidkaigi.confsched2019.session.ui.store

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.dummySessionData
import io.github.droidkaigi.confsched2019.ext.android.CoroutinePlugin
import io.github.droidkaigi.confsched2019.ext.android.changedForever
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.Session
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

class SessionsStoreTest {
    @JvmField @Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        CoroutinePlugin.mainDispatcherHandler = { Dispatchers.Default }
    }

    @Test fun loadingState() = runBlocking<Unit> {
        val dispatcher = Dispatcher()
        val sessionPagesStore = SessionsStore(dispatcher)
        val observer = mockk<(LoadingState?) -> Unit>(relaxed = true)

        sessionPagesStore.loadingState.changedForever(observer)

        dispatcher.dispatch(Action.SessionLoadingStateChanged(LoadingState.LOADING))
        dispatcher.dispatch(Action.SessionLoadingStateChanged(LoadingState.LOADED))

        sessionPagesStore.isLoading shouldBe false
        verifySequence {
            observer(LoadingState.LOADING)
            observer(LoadingState.LOADED)
        }
    }

    @Test fun sessions() = runBlocking<Unit> {
        val dispatcher = Dispatcher()
        val sessionPagesStore = SessionsStore(dispatcher)
        val observer: (List<Session>) -> Unit = mockk(relaxed = true)
        sessionPagesStore.sessions.changedForever(observer)

        dispatcher.dispatch(
            Action.SessionsLoaded(SessionContents.EMPTY.copy(sessions = dummySessionData()))
        )

        verifySequence {
            observer(listOf())
            observer(dummySessionData())
        }
    }
}
