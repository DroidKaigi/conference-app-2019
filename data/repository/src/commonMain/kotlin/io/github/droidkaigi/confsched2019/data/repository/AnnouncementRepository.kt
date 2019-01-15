package io.github.droidkaigi.confsched2019.data.repository

import io.github.droidkaigi.confsched2019.model.Announcement

interface AnnouncementRepository {
    suspend fun announcements(): List<Announcement>
    suspend fun refresh()
}
