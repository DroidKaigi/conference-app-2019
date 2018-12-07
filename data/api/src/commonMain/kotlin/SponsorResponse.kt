package io.github.droidkaigi.confsched2019.data.api.response

interface SponsorResponse {
    val platinum: List<SponsorItemResponse>
    val gold: List<SponsorItemResponse>
    val support: List<SponsorItemResponse>
    val tech: List<SponsorItemResponse>

}
