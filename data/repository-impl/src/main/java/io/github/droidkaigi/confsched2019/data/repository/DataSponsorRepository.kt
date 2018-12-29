package io.github.droidkaigi.confsched2019.data.repository

import io.github.droidkaigi.confsched2019.data.api.DroidKaigiApi
import io.github.droidkaigi.confsched2019.data.db.SponsorDatabase
import io.github.droidkaigi.confsched2019.data.db.entity.SponsorEntity
import io.github.droidkaigi.confsched2019.model.Sponsor
import io.github.droidkaigi.confsched2019.model.SponsorCategory
import javax.inject.Inject

class DataSponsorRepository @Inject constructor(
    private val api: DroidKaigiApi,
    private val sponsorDatabase: SponsorDatabase
) : SponsorRepository {
    override suspend fun sponsors() = sponsorDatabase
        .sponsors()
        .groupBy { it.categoryIndex }
        .map { (_, sponsors) ->
            val category = sponsors.first().category
            val index = sponsors.first().categoryIndex
            SponsorCategory(
                category,
                index,
                sponsors.map(SponsorEntity::toSponsor)
            )
        }

    override suspend fun refresh() {
        val response = api.getSponsors()
        sponsorDatabase.save(response)
    }
}

private fun SponsorEntity.toSponsor(): Sponsor = Sponsor(name, url, image)
