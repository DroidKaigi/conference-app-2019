package io.github.droidkaigi.confsched2019

import com.soywiz.klock.DateTime
import com.soywiz.klock.hours
import com.soywiz.klock.minutes
import io.github.droidkaigi.confsched2019.model.Announcement

private val startTime =
    DateTime.createAdjusted(2019, 2, 7, 10, 0).toOffsetUnadjusted(9.hours).utc

fun dummyAnnouncementsData(): List<Announcement> {
    return listOf(
        Announcement(
            1L,
            "title1",
            "content1",
            startTime,
            Announcement.Type.NOTIFICATION
        ),
        Announcement(
            2L,
            "title2",
            "content2",
            startTime + 30.minutes,
            Announcement.Type.ALERT
        ),
        Announcement(
            3L,
            "title3",
            "content3",
            startTime + 60.minutes,
            Announcement.Type.FEEDBACK
        )
    )
}
