package io.github.droidkaigi.confsched2019.action

import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.SessionTab
import io.github.droidkaigi.confsched2019.model.SystemProperty

sealed class Action {
    class AllSessionLoadingStateChanged(val loadingState: LoadingState) : Action()
    class AnnouncementLoadingStateChanged(val loadingState: LoadingState) : Action()
    data class AllSessionLoaded(val sessions: List<Session>) : Action()
    object UserRegistered : Action()
    data class SessionLoaded(val session: Session.SpeechSession) : Action()
    data class SessionTabSelected(val sessionTab: SessionTab) : Action()
    class SystemPropertyLoaded(val system: SystemProperty) : Action()
}
