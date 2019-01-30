package io.github.droidkaigi.confsched2019.action

import io.github.droidkaigi.confsched2019.model.*

sealed class Action {
    class Error(val msg: ErrorMessage) : Action()

    data class SessionLoadingStateChanged(val loadingState: LoadingState) : Action()
    data class SessionContentsLoaded(
        val sessionContents: SessionContents
    ) : Action()

    data class SessionsLoaded(
        val sessions: List<Session>
    ) : Action()

    data class SessionPageSelected(val sessionPage: SessionPage) : Action()
    class SystemPropertyLoaded(val system: SystemProperty) : Action()

    open class FilterChange<T>(val value: T, open val checked: Boolean) : Action()
    data class RoomFilterChanged(
        val room: Room,
        override val checked: Boolean
    ) : FilterChange<Room>(room, checked)

    data class CategoryFilterChanged(
        val category: Category,
        override val checked: Boolean
    ) : FilterChange<Category>(category, checked)

    data class LangFilterChanged(
        val lang: Lang,
        override val checked: Boolean
    ) : FilterChange<Lang>(lang, checked)

    data class LangSupportFilterChanged(
        val langSupport: LangSupport,
        override val checked: Boolean
    ) : FilterChange<LangSupport>(langSupport, checked)

    data class AudienceCategoryFilterChanged(
        val audienceCategory: AudienceCategory,
        override val checked: Boolean
    ) : FilterChange<AudienceCategory>(audienceCategory, checked)

    class FilterCleared : Action()
    class BottomSheetFilterToggled(val page: SessionPage) : Action()
    class BottomSheetFilterStateChanged(val page: SessionPage, val bottomSheetState: Int) : Action()

    data class SearchResultLoaded(val searchResult: SearchResult) : Action()

    data class StaffSearchResultLoaded(val searchResult: StaffSearchResult) : Action()
    data class StaffSearchLoadingStateChanged(val loadingState: LoadingState) : Action()
    data class StaffLoaded(val staffContents: StaffContents) : Action()

    object UserRegistered : Action()

    data class AnnouncementLoadingStateChanged(val loadingState: LoadingState) : Action()
    data class AnnouncementLoaded(val announcements: List<Announcement>) : Action()

    data class ContributorLoadingStateChanged(val loadingState: LoadingState) : Action()
    data class ContributorLoaded(val contributorList: ContributorContents) : Action()

    data class SponsorLoadingStateChanged(val loadingState: LoadingState) : Action()
    data class SponsorLoaded(val sponsors: List<SponsorCategory>) : Action()

    data class SessionSurveyLoadingStateChanged(val loadingState: LoadingState) : Action()
    data class SessionSurveyLoaded(val sessionFeedback: SessionFeedback) : Action()
    object SessionSurveySubmitted : Action()

    class FloorMapLoadingStateChanged(val loadingState: LoadingState) : Action()
}
