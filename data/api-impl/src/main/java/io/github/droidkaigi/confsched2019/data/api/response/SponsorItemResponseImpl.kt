package io.github.droidkaigi.confsched2019.data.api.response

import kotlinx.serialization.Serializable

@Serializable
data class SponsorItemResponseImpl(
    override val name: String,
    override val url: String,
    override val image: String
) : SponsorItemResponse

