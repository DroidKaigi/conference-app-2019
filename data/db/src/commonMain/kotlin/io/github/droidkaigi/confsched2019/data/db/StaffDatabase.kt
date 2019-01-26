package io.github.droidkaigi.confsched2019.data.db

import io.github.droidkaigi.confsched2019.data.api.response.StaffResponse
import io.github.droidkaigi.confsched2019.data.db.entity.StaffEntity

interface StaffDatabase {
    suspend fun staffs(): List<StaffEntity>
    suspend fun save(apiResponse: StaffResponse)
}
