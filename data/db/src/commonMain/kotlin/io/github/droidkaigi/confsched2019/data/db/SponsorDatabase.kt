package io.github.droidkaigi.confsched2019.data.db

import io.github.droidkaigi.confsched2019.data.api.response.SponsorResponse
import io.github.droidkaigi.confsched2019.data.db.entity.SponsorEntity

interface SponsorDatabase {
    suspend fun sponsors(): List<SponsorEntity>
    suspend fun save(apiResponse: SponsorResponse)
}
