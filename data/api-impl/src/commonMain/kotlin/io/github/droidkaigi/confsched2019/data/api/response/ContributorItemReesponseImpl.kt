package io.github.droidkaigi.confsched2019.data.api.response

import kotlinx.serialization.Serializable

@Serializable
data class ContributorItemReesponseImpl(
    override val id: Int?,
    override val name: String?,
    override val iconUrl: String?,
    override val profileUrl: String?,
    override val type: String?
) : ContributorItemResponse
