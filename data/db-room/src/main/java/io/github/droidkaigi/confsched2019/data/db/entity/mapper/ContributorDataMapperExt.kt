package io.github.droidkaigi.confsched2019.data.db.entity.mapper

import io.github.droidkaigi.confsched2019.data.api.response.ContributorItemResponse
import io.github.droidkaigi.confsched2019.data.db.entity.ContributorEntityImpl

fun List<ContributorItemResponse>.toContributorEntityImpl(): List<ContributorEntityImpl> = map {
    it.toContributorEntityImpl()
}

fun ContributorItemResponse.toContributorEntityImpl(): ContributorEntityImpl {
    return ContributorEntityImpl(
        id = requireNotNull(id),
        name = requireNotNull(name),
        iconUrl = iconUrl,
        profileUrl = profileUrl,
        author = requireNotNull(type)
    )
}
