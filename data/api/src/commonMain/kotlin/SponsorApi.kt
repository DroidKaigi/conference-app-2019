package io.github.droidkaigi.confsched2019.data.api

import io.github.droidkaigi.confsched2019.data.api.response.SponsorResponse

interface SponsorApi {
    suspend fun getSponsors(): SponsorResponse
}
