package io.github.droidkaigi.confsched2019.data.repository

import io.github.droidkaigi.confsched2019.data.api.DroidKaigiApi
import io.github.droidkaigi.confsched2019.data.db.ContributorDatabase
import io.github.droidkaigi.confsched2019.data.db.entity.ContributorEntity
import io.github.droidkaigi.confsched2019.model.Contributor
import io.github.droidkaigi.confsched2019.model.ContributorContents
import javax.inject.Inject

class DataContributorRepository @Inject constructor(
    private val api: DroidKaigiApi,
    private val contributorDatabase: ContributorDatabase
) : ContributorRepository {
    override suspend fun contributorContents(): ContributorContents =
        ContributorContents(contributorList())

    override suspend fun refresh() {
        val response = api.getContributorList()
        contributorDatabase.save(response)
    }

    private suspend fun contributorList() = contributorDatabase
        .contributorList()
        .map { it.toContributor() }
}

private fun ContributorEntity.toContributor(): Contributor =
    Contributor(id, name, iconUrl, profileUrl, author)
