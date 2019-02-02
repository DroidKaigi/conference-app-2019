package io.github.droidkaigi.confsched2019

import io.github.droidkaigi.confsched2019.model.Sponsor
import io.github.droidkaigi.confsched2019.model.SponsorCategory

fun dummySponsorCategoriesData():List<SponsorCategory> {
    return listOf(
        SponsorCategory(
            category = SponsorCategory.Category.PLATINUM,
            index = 1,
            sponsors = listOf(
                Sponsor(
                    name = "platinum1",
                    url = "",
                    image = ""
                )
            )
        ),
        SponsorCategory(
            category = SponsorCategory.Category.GOLD,
            index = 2,
            sponsors = listOf(
                Sponsor(
                    name = "gold",
                    url = "",
                    image = ""
                )
            )
        ),
        SponsorCategory(
            category = SponsorCategory.Category.SUPPORT,
            index = 3,
            sponsors = listOf(
                Sponsor(
                    name = "support",
                    url = "",
                    image = ""
                )
            )
        ),
        SponsorCategory(
            category = SponsorCategory.Category.TECH,
            index = 4,
            sponsors = listOf(
                Sponsor(
                    name = "tech",
                    url = "",
                    image = ""
                )
            )
        )
    )
}
