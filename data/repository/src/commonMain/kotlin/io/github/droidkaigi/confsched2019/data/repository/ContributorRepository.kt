package io.github.droidkaigi.confsched2019.data.repository

import io.github.droidkaigi.confsched2019.model.ContributorContents

interface ContributorRepository {
    suspend fun contributorContents(): ContributorContents
    suspend fun refresh(): Unit
}
