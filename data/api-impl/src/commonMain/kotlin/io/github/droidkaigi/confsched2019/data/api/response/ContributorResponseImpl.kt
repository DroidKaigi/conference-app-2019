package io.github.droidkaigi.confsched2019.data.api.response

import kotlinx.serialization.Serializable

@Serializable
data class ContributorResponseImpl(
    override val contributorList: List<ContributorItemReesponseImpl>
) : ContributorResponse
