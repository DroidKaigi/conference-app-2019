package io.github.droidkaigi.confsched2019.data.repository

import io.github.droidkaigi.confsched2019.data.api.DroidKaigiApi
import io.github.droidkaigi.confsched2019.data.db.StaffDatabase
import io.github.droidkaigi.confsched2019.data.db.entity.StaffEntity
import io.github.droidkaigi.confsched2019.model.Staff
import io.github.droidkaigi.confsched2019.model.StaffContents
import javax.inject.Inject

class DataStaffRepository @Inject constructor(
    private val api: DroidKaigiApi,
    private val staffDatabase: StaffDatabase
) : StaffRepository {
    override suspend fun staffContents() = StaffContents(staffs())

    override suspend fun refresh() {
        val response = api.getStaffs()
        staffDatabase.save(response)
    }

    private suspend fun staffs() = staffDatabase
        .staffs()
        .map { it.toStaff() }
}

private fun StaffEntity.toStaff(): Staff = Staff(id, name, iconUrl, profileUrl)
