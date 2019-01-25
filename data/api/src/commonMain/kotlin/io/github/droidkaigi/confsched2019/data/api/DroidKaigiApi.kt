package io.github.droidkaigi.confsched2019.data.api

import io.github.droidkaigi.confsched2019.data.api.parameter.LangParameter
import io.github.droidkaigi.confsched2019.data.api.response.AnnouncementListResponse
import io.github.droidkaigi.confsched2019.data.api.response.Response
import io.github.droidkaigi.confsched2019.data.api.response.SponsorResponse
import io.github.droidkaigi.confsched2019.data.api.response.StaffResponse

interface DroidKaigiApi {
    suspend fun getSessions(): Response

    fun getSessions(callback: (response: Response) -> Unit, onError: (error: Exception) -> Unit)

    suspend fun getSponsors(): SponsorResponse

    suspend fun getAnnouncements(lang: LangParameter): AnnouncementListResponse

    suspend fun getStaffs(): StaffResponse
}
