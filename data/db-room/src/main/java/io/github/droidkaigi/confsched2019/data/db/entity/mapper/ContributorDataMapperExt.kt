package io.github.droidkaigi.confsched2019.data.db.entity.mapper

import io.github.droidkaigi.confsched2019.data.db.entity.ContributorEntityImpl
import io.github.droidkaigi.confsched2019.data.api.response.ContributorItemResponse

fun List<ContributorItemResponse>.toContributorEntityImpl(): List<ContributorEntityImpl> = map {
    it.toContributorEntityImpl()
}

fun ContributorItemResponse.toContributorEntityImpl(): ContributorEntityImpl{
    return ContributorEntityImpl(
        id = id,
        name = name,
        iconUrl = iconUrl,
        profileUrl = profileUrl,
        author = requireNotNull(type)
    )
}
