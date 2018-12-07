package io.github.droidkaigi.confsched2019.data.api.response

data class SponsorResponseImpl(
    override val platinum: List<SponsorItemResponse>,
    override val gold: List<SponsorItemResponse>,
    override val support: List<SponsorItemResponse>,
    override val tech: List<SponsorItemResponse>
) : SponsorResponse
