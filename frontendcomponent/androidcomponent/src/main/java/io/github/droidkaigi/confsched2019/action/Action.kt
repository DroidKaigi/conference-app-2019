package io.github.droidkaigi.confsched2019.action

import io.github.droidkaigi.confsched2019.model.Lang
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.Post
import io.github.droidkaigi.confsched2019.model.Room
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.SessionContents
import io.github.droidkaigi.confsched2019.model.SessionTab
import io.github.droidkaigi.confsched2019.model.SystemProperty
import io.github.droidkaigi.confsched2019.model.Topic

sealed class Action {
    class AllSessionLoadingStateChanged(val loadingState: LoadingState) : Action()
    data class AllSessionLoaded(
        val sessionContents: SessionContents
    ) : Action()

    object UserRegistered : Action()
    data class SessionLoaded(val session: Session.SpeechSession) : Action()
    data class SessionTabSelected(val sessionTab: SessionTab) : Action()
    class SystemPropertyLoaded(val system: SystemProperty) : Action()

    data class RoomFilterChanged(val room: Room, val checked: Boolean) : Action()
    data class TopicFilterChanged(val topic: Topic, val checked: Boolean) : Action()
    data class LangFilterChanged(val lang: Lang, val checked: Boolean) : Action()
    class FilterCleared : Action()

    class AnnouncementLoadingStateChanged(val loadingState: LoadingState) : Action()
    class AnnouncementLoaded(val posts: List<Post>) : Action()
}
