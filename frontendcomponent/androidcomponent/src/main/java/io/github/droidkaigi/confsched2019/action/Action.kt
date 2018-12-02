package io.github.droidkaigi.confsched2019.action

import io.github.droidkaigi.confsched2019.model.ErrorMessage
import io.github.droidkaigi.confsched2019.model.Lang
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.Post
import io.github.droidkaigi.confsched2019.model.Room
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.SessionContents
import io.github.droidkaigi.confsched2019.model.SessionPage
import io.github.droidkaigi.confsched2019.model.Speaker
import io.github.droidkaigi.confsched2019.model.Sponsor
import io.github.droidkaigi.confsched2019.model.SystemProperty
import io.github.droidkaigi.confsched2019.model.Topic

sealed class Action {
    data class SessionRefreshStateChanged(val loadingState: LoadingState) : Action()
    class SessionLoadingStateChanged(val loadingState: LoadingState) : Action()
    data class SessionsLoaded(
        val sessionContents: SessionContents
    ) : Action()

    data class SpeakerLoaded(val speaker: Speaker) : Action()
    data class SessionLoaded(val session: Session.SpeechSession) : Action()
    data class SessionPageSelected(val sessionPage: SessionPage) : Action()
    class SystemPropertyLoaded(val system: SystemProperty) : Action()

    open class FilterChange<T>(val value: T, open val checked: Boolean) : Action()
    data class RoomFilterChanged(
        val room: Room,
        override val checked: Boolean
    ) : FilterChange<Room>(room, checked)

    data class TopicFilterChanged(
        val topic: Topic,
        override val checked: Boolean
    ) : FilterChange<Topic>(topic, checked)

    data class LangFilterChanged(
        val lang: Lang,
        override val checked: Boolean
    ) : FilterChange<Lang>(lang, checked)

    class FilterCleared : Action()
    class BottomSheetFilterToggled(val page: SessionPage) : Action()
    class BottomSheetFilterStateChanged(val page: SessionPage, val bottomSheetState: Int) : Action()

    object UserRegistered : Action()

    class AnnouncementLoadingStateChanged(val loadingState: LoadingState) : Action()
    class AnnouncementLoaded(val posts: List<Post>) : Action()
    class Error(val msg: ErrorMessage) : Action()
    class SponsorLoadingStateChanged(val loadingState: LoadingState) : Action()
    class SponsorLoaded(val sponsors: List<Sponsor>) : Action()
}
