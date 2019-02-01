package io.github.droidkaigi.confsched2019.data.db.entity.mapper

import io.github.droidkaigi.confsched2019.data.api.response.ContributorItemResponse
import io.github.droidkaigi.confsched2019.data.db.entity.ContributorEntityImpl

fun List<ContributorItemResponse>.toContributorEntities(): List<ContributorEntityImpl> = map {
    it.toContributorEntities()
}

fun ContributorItemResponse.toContributorEntities(): ContributorEntityImpl {
    return ContributorEntityImpl(
        id = id,
        name = name,
        iconUrl = iconUrl,
        profileUrl = profileUrl,
        type = type
    )
}
