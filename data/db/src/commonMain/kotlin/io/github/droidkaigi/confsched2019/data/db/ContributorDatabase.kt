package io.github.droidkaigi.confsched2019.data.db

import io.github.droidkaigi.confsched2019.data.api.response.ContributorResponse
import io.github.droidkaigi.confsched2019.data.db.entity.ContributorEntity

interface ContributorDatabase {
    suspend fun contributorList(): List<ContributorEntity>
    suspend fun save(apiResponse: ContributorResponse)
}
