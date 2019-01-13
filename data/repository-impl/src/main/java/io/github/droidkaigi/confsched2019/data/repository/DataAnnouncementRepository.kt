package io.github.droidkaigi.confsched2019.data.repository

import com.soywiz.klock.DateTime
import io.github.droidkaigi.confsched2019.data.api.DroidKaigiApi
import io.github.droidkaigi.confsched2019.data.api.parameter.LangParameter
import io.github.droidkaigi.confsched2019.data.db.AnnouncementDatabase
import io.github.droidkaigi.confsched2019.model.Announcement
import io.github.droidkaigi.confsched2019.model.Lang
import io.github.droidkaigi.confsched2019.model.defaultLang
import kotlinx.coroutines.coroutineScope
import java.util.Locale
import javax.inject.Inject

class DataAnnouncementRepository @Inject constructor(
    private val droidKaigiApi: DroidKaigiApi,
    private val announcementDatabase: AnnouncementDatabase
) : AnnouncementRepository {
    override suspend fun announcements(): List<Announcement> = coroutineScope {
        announcementDatabase.announcementsByLang(defaultLang().toParameter().name)
            .sortedBy { it.publishedAt }
            .map {
                Announcement(
                    id = it.id,
                    type = Announcement.Type.valueOf(it.type.toUpperCase(Locale.US)),
                    title = it.title,
                    publishedAt = DateTime(it.publishedAt),
                    content = it.content
                )
            }
    }

    override suspend fun refresh() {
        val announcements = droidKaigiApi.getAnnouncements(defaultLang().toParameter())
        announcementDatabase.save(announcements)
    }

    private fun Lang.toParameter(): LangParameter {
        return when (this) {
            Lang.EN -> LangParameter.EN
            Lang.JA -> LangParameter.JP
        }
    }
}
