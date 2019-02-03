package io.github.droidkaigi.confsched2019.survey.ui.actioncreator

import androidx.lifecycle.Lifecycle
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.data.repository.SessionRepository
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.dummySessionFeedbackData
import io.github.droidkaigi.confsched2019.ext.android.CoroutinePlugin
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.widget.component.TestLifecycleOwner
import io.github.droidkaigi.confsched2019.dummySpeechSessionData
import io.mockk.MockKAnnotations
import io.mockk.Ordering
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class SessionSurveyActionCreatorTest {
    @RelaxedMockK lateinit var dispatcher: Dispatcher
    @RelaxedMockK lateinit var sessionRepository: SessionRepository

    private val lifecycleOwner = TestLifecycleOwner().handleEvent(Lifecycle.Event.ON_RESUME)
    private lateinit var sessionSurveyActionCreator: SessionSurveyActionCreator

    private val dummySessionId = "12345"

    @Before fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        CoroutinePlugin.mainDispatcherHandler = { Dispatchers.Default }

        sessionSurveyActionCreator = SessionSurveyActionCreator(
            dispatcher,
            sessionRepository,
            lifecycleOwner.lifecycle
        )
    }

    @Test fun load() = runBlocking {
        coEvery {
            sessionRepository.sessionFeedback(dummySessionId)
        } returns dummySessionFeedbackData()

        sessionSurveyActionCreator.load(dummySessionId)

        coVerify(ordering = Ordering.ORDERED) {
            dispatcher.dispatch(Action.SessionSurveyLoadingStateChanged(LoadingState.LOADING))
            sessionRepository.sessionFeedback(dummySessionId)
            dispatcher.dispatch(Action.SessionSurveyLoadingStateChanged(LoadingState.LOADED))
        }
    }

    @Test fun submit() = runBlocking {

        sessionSurveyActionCreator.submit(dummySpeechSessionData(), dummySessionFeedbackData())

        coVerify(ordering = Ordering.ORDERED) {
            dispatcher.dispatch(Action.SessionSurveyLoadingStateChanged(LoadingState.LOADING))
            sessionRepository.submitSessionFeedback(
                dummySpeechSessionData(),
                dummySessionFeedbackData()
            )
            sessionRepository.saveSessionFeedback(dummySessionFeedbackData())
            dispatcher.dispatch(Action.SessionSurveyLoadingStateChanged(LoadingState.LOADED))
            dispatcher.dispatch(Action.SessionSurveyLoaded(dummySessionFeedbackData()))
        }
    }

    @Test fun changeSessionFeedback() = runBlocking {

        sessionSurveyActionCreator.changeSessionFeedback(dummySessionFeedbackData())

        coVerify(ordering = Ordering.ORDERED) {
            dispatcher.dispatch(Action.SessionSurveyLoaded(dummySessionFeedbackData()))
        }
    }
}

