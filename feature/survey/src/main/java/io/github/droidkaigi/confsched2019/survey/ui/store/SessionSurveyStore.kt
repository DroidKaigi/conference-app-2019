package io.github.droidkaigi.confsched2019.survey.ui.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.requireValue
import io.github.droidkaigi.confsched2019.ext.android.toLiveData
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.SessionFeedback
import kotlinx.coroutines.channels.map
import javax.inject.Inject

class SessionSurveyStore @Inject constructor(
    dispatcher: Dispatcher
) : ViewModel() {
    val loadingState: LiveData<LoadingState> = dispatcher
        .subscribe<Action.SessionSurveyLoadingStateChanged>()
        .map { it.loadingState }
        .toLiveData(LoadingState.INITIALIZED)

    val sessionFeedback: LiveData<SessionFeedback> = dispatcher
        .subscribe<Action.SessionSurveyLoaded>()
        .map { it.sessionFeedback }
        .toLiveData(SessionFeedback.EMPTY)
    val totalEvaluation get() = sessionFeedback.requireValue().totalEvaluation
    val relevancy get() = sessionFeedback.requireValue().relevancy
    val asExpected get() = sessionFeedback.requireValue().asExpected
    val difficulty get() = sessionFeedback.requireValue().difficulty
    val knowledgeable get() = sessionFeedback.requireValue().knowledgeable
    val comment get() = sessionFeedback.requireValue().comment
}
