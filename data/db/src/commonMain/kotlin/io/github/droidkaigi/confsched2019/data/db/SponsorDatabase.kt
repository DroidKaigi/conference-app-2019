package io.github.droidkaigi.confsched2019.data.db

import io.github.droidkaigi.confsched2019.data.api.response.SponsorResponse
import io.github.droidkaigi.confsched2019.data.db.entity.SponsorEntity
import kotlinx.coroutines.channels.ReceiveChannel

interface SponsorDatabase {
    fun sponsorChannel(): ReceiveChannel<List<SponsorEntity>>
    suspend fun save(apiResponse: SponsorResponse)
}
