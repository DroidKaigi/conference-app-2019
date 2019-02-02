package io.github.droidkaigi.confsched2019

import com.soywiz.klock.DateTime
import com.soywiz.klock.hours
import io.github.droidkaigi.confsched2019.model.SponsorCategory
import io.github.droidkaigi.confsched2019.model.Sponsor

private val startTime =
    DateTime.createAdjusted(2019, 2, 7, 10, 0).toOffsetUnadjusted(9.hours).utc

fun dummySponsorsData(): List<SponsorCategory> {
    return listOf(
        SponsorCategory(
            SponsorCategory.Category.PLATINUM,
            1,
            listOf(
                Sponsor(
                    "name1",
                    "url1",
                    "image1"
                )
            )
        ),
        SponsorCategory(
            SponsorCategory.Category.GOLD,
            2,
            listOf(
                Sponsor(
                    "name2",
                    "url2",
                    "image2"
                )
            )
        ),
        SponsorCategory(
            SponsorCategory.Category.SUPPORT,
            3,
            listOf(
                Sponsor(
                    "name3",
                    "url3",
                    "image3"
                )
            )
        )
    )
}
