package io.github.droidkaigi.confsched2019.survey.ui.store

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.dummySessionFeedbackData
import io.github.droidkaigi.confsched2019.ext.android.CoroutinePlugin
import io.github.droidkaigi.confsched2019.ext.android.changedForever
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.SessionFeedback
import io.github.droidkaigi.confsched2019.widget.component.DirectDispatcher
import io.mockk.MockKAnnotations
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SessionSurveyStoreTest {
    @JvmField @Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        CoroutinePlugin.mainDispatcherHandler = { DirectDispatcher }
    }

    @Test fun loadingState() = runBlocking {
        val dispatcher = Dispatcher()
        val sessionSurveyStore = SessionSurveyStore(dispatcher)
        val observer = mockk<(LoadingState?) -> Unit>(relaxed = true)

        sessionSurveyStore.loadingState.changedForever(observer)
        verify { observer(LoadingState.INITIALIZED) }

        dispatcher.dispatch(Action.SessionSurveyLoadingStateChanged(LoadingState.LOADING))
        verify { observer(LoadingState.LOADING) }

        dispatcher.dispatch(Action.SessionSurveyLoadingStateChanged(LoadingState.LOADED))
        verify { observer(LoadingState.LOADED) }
    }

    @Test fun sessionFeedback() = runBlocking {
        val dispatcher = Dispatcher()
        val sessionSurveyStore = SessionSurveyStore(dispatcher)
        val observer = mockk<(SessionFeedback) -> Unit>(relaxed = true)

        sessionSurveyStore.sessionFeedback.changedForever(observer)
        verify { observer(SessionFeedback.EMPTY) }

        val dummySessionFeedback = dummySessionFeedbackData()
        dispatcher.dispatch(
            Action.SessionSurveyLoaded(dummySessionFeedback)
        )
        verify { observer(dummySessionFeedback) }
    }
}
