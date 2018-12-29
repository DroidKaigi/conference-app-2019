package io.github.droidkaigi.confsched2019.data.db.entity.mapper

import io.github.droidkaigi.confsched2019.data.api.response.SponsorItemResponse
import io.github.droidkaigi.confsched2019.data.db.entity.SponsorEntityImpl

fun List<SponsorItemResponse>.toSponsorEntities(): List<SponsorEntityImpl> =
    map { responseSponsor ->
        SponsorEntityImpl(
            responseSponsor.name,
            responseSponsor.url,
            responseSponsor.image
        )
    }
