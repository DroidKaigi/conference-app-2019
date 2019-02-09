package io.github.droidkaigi.confsched2019.survey.ui.store

import androidx.lifecycle.LiveData
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.requireValue
import io.github.droidkaigi.confsched2019.ext.toLiveData
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.SessionFeedback
import io.github.droidkaigi.confsched2019.store.Store
import kotlinx.coroutines.channels.map
import javax.inject.Inject

class SessionSurveyStore @Inject constructor(
    dispatcher: Dispatcher
) : Store() {
    val loadingState: LiveData<LoadingState> = dispatcher
        .subscribe<Action.SessionSurveyLoadingStateChanged>()
        .map { it.loadingState }
        .toLiveData(this, LoadingState.INITIALIZED)

    val sessionFeedback: LiveData<SessionFeedback> = dispatcher
        .subscribe<Action.SessionSurveyLoaded>()
        .map { it.sessionFeedback }
        .toLiveData(this, SessionFeedback.EMPTY)
    val totalEvaluation get() = sessionFeedback.requireValue().totalEvaluation
    val relevancy get() = sessionFeedback.requireValue().relevancy
    val asExpected get() = sessionFeedback.requireValue().asExpected
    val difficulty get() = sessionFeedback.requireValue().difficulty
    val knowledgeable get() = sessionFeedback.requireValue().knowledgeable
    val comment get() = sessionFeedback.requireValue().comment
    val submitted get() = sessionFeedback.requireValue().submitted
}
