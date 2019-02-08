package io.github.droidkaigi.confsched2019

import io.github.droidkaigi.confsched2019.model.Contributor
import io.github.droidkaigi.confsched2019.model.ContributorContents

fun dummyContributorsData(): ContributorContents {
    return ContributorContents(listOf(
        Contributor(
            1386930,
            "takahirom",
            "https://github.com/takahirom.png?size=100",
            "https://github.com/takahirom",
            "author"
        ),
        Contributor(
            933895,
            "rince",
            "https://github.com/rince.png?size=100",
            "https://github.com/rince",
            "contributor"
        )
    ))
}
