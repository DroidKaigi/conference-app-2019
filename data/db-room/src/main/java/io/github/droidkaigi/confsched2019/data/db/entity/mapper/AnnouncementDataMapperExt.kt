package io.github.droidkaigi.confsched2019.data.db.entity.mapper

import com.soywiz.klock.DateFormat
import com.soywiz.klock.parse
import io.github.droidkaigi.confsched2019.data.api.response.AnnouncementResponse
import io.github.droidkaigi.confsched2019.data.db.entity.AnnouncementEntityImpl

private val dateFormat: DateFormat =
    DateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")

fun List<AnnouncementResponse>.toAnnouncementEntities(): List<AnnouncementEntityImpl> = map {
    it.toAnnouncementEntityImpl()
}

fun AnnouncementResponse.toAnnouncementEntityImpl(): AnnouncementEntityImpl {
    return AnnouncementEntityImpl(
        id = id,
        content = content,
        publishedAt = dateFormat.parse(publishedAt).utc.unixMillisLong,
        lang = lang,
        title = title,
        type = type
    )
}
