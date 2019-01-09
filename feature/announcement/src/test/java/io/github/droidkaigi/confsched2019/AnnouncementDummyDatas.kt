package io.github.droidkaigi.confsched2019

import com.soywiz.klock.DateTime
import com.soywiz.klock.minutes
import io.github.droidkaigi.confsched2019.model.Announcement

private val startTime = DateTime.createAdjusted(2019, 2, 7, 10, 0)
fun dummyAnnouncementsData(): List<Announcement> {
    return listOf(
        Announcement(
            "title1",
            "content1",
            startTime,
            Announcement.Type.NOTIFICATION
        ),
        Announcement(
            "title2",
            "content2",
            startTime + 30.minutes,
            Announcement.Type.ALERT
        ),
        Announcement(
            "title3",
            "content3",
            startTime + 60.minutes,
            Announcement.Type.FEEDBACK
        )
    )
}
