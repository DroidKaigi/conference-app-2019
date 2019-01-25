package io.github.droidkaigi.confsched2019.data.repository

import io.github.droidkaigi.confsched2019.model.StaffContents

interface StaffRepository {
    suspend fun staffContents(): StaffContents
    suspend fun refresh()
}
