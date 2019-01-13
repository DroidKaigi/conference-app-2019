package io.github.droidkaigi.confsched2019.data.db

import io.github.droidkaigi.confsched2019.data.api.response.AnnouncementResponse
import io.github.droidkaigi.confsched2019.data.db.entity.AnnouncementEntity

interface AnnouncementDatabase {
    suspend fun announcementsByLang(lang: String): List<AnnouncementEntity>
    suspend fun save(apiResponse: List<AnnouncementResponse>)
}
