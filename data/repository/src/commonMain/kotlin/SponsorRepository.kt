package io.github.droidkaigi.confsched2019.data.repository

import io.github.droidkaigi.confsched2019.model.Sponsor
import kotlinx.coroutines.channels.ReceiveChannel

interface SponsorRepository {
    suspend fun sponsors(): List<Sponsor>
    suspend fun refresh()
}
