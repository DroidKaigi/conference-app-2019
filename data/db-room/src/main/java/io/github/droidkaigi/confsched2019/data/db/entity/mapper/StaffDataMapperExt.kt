package io.github.droidkaigi.confsched2019.data.db.entity.mapper

import io.github.droidkaigi.confsched2019.data.api.response.StaffItemResponse
import io.github.droidkaigi.confsched2019.data.db.entity.StaffEntityImpl

fun List<StaffItemResponse>.toStaffEntities(): List<StaffEntityImpl> = map {
    it.toStaffEntityImpl()
}

fun StaffItemResponse.toStaffEntityImpl(): StaffEntityImpl {
    return StaffEntityImpl(
        id = requireNotNull(id),
        name = requireNotNull(name),
        iconUrl = iconUrl,
        profileUrl = profileUrl
    )
}
