package io.github.droidkaigi.confsched2019.data.api

import io.github.droidkaigi.confsched2019.data.api.response.AnnouncementResponse
import io.github.droidkaigi.confsched2019.data.api.response.Response
import io.github.droidkaigi.confsched2019.data.api.response.SponsorResponse
import io.github.droidkaigi.confsched2019.model.Lang

interface DroidKaigiApi {
    suspend fun getSessions(): Response

    suspend fun getSponsors(): SponsorResponse

    suspend fun getAnnouncements(lang: Lang): List<AnnouncementResponse>
}
