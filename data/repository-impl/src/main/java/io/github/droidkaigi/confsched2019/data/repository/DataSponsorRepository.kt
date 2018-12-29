package io.github.droidkaigi.confsched2019.data.repository

import io.github.droidkaigi.confsched2019.data.api.SponsorApi
import io.github.droidkaigi.confsched2019.data.db.SponsorDatabase
import io.github.droidkaigi.confsched2019.data.db.entity.SponsorEntity
import io.github.droidkaigi.confsched2019.model.Sponsor
import io.github.droidkaigi.confsched2019.data.firestore.FireStore
import io.github.droidkaigi.confsched2019.model.SponsorCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

class DataSponsorRepository @Inject constructor(
    private val sponsorApi: SponsorApi,
    private val sponsorDatabase: SponsorDatabase
) : SponsorRepository {
    override suspend fun sponsors() = sponsorDatabase
        .sponsorChannel()
        .receive()
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
        val response = sponsorApi.getSponsors()
        sponsorDatabase.save(response)
    }
}

private fun SponsorEntity.toSponsor(): Sponsor = Sponsor(name, url, image)
