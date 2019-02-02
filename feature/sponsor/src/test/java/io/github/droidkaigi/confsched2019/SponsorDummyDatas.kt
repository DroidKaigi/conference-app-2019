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
                    name = "mercari",
                    url = "https://tech.mercari.com/",
                    image = "https://droidkaigi.jp/2019/images/sponsors/25_mercari.png"
                )
            )
        ),
        SponsorCategory(
            category = SponsorCategory.Category.GOLD,
            index = 2,
            sponsors = listOf(
                Sponsor(
                    name = "google developers",
                    url = "https://developers.google.com/",
                    image = "https://droidkaigi.jp/2019/images/sponsors/aa_google.png"
                )
            )
        ),
        SponsorCategory(
            category = SponsorCategory.Category.SUPPORT,
            index = 3,
            sponsors = listOf(
                Sponsor(
                    name = "deploygate",
                    url = "https://deploygate.com/",
                    image = "https://droidkaigi.jp/2019/images/sponsors/42_deploygate.png"
                )
            )
        ),
        SponsorCategory(
            category = SponsorCategory.Category.TECH,
            index = 4,
            sponsors = listOf(
                Sponsor(
                    name = "dwango",
                    url = "http://dwango.co.jp/",
                    image = "https://droidkaigi.jp/2019/images/sponsors/58_dwango.png"
                )
            )
        )
    )
}
