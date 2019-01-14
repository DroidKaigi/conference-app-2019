package io.github.droidkaigi.confsched2019.data.api

import io.github.droidkaigi.confsched2019.data.api.parameter.LangParameter
import io.github.droidkaigi.confsched2019.data.api.response.AnnouncementListResponse
import io.github.droidkaigi.confsched2019.data.api.response.Response
import io.github.droidkaigi.confsched2019.data.api.response.SponsorResponse

interface DroidKaigiApi {
    suspend fun getSessions(): Response

    suspend fun getSponsors(): SponsorResponse

    suspend fun getAnnouncements(lang: LangParameter): AnnouncementListResponse
}
