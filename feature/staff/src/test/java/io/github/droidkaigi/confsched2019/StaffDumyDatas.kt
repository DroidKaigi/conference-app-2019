package io.github.droidkaigi.confsched2019

import io.github.droidkaigi.confsched2019.model.Staff
import io.github.droidkaigi.confsched2019.model.StaffContents
import io.github.droidkaigi.confsched2019.model.StaffSearchResult

private val resultList = listOf(
    Staff(
        "id1",
        "name1",
        "https://example.com",
        "https://example.com"
    ),
    Staff(
        "id2",
        "name2",
        "https://example.com",
        null
    ),
    Staff(
        "id3",
        "name3",
        null,
        null
    )
)

fun dummySearchResultData(): StaffSearchResult {
    return StaffSearchResult(
        resultList,
        "query"
    )
}

fun dummyStaffContentsData(): StaffContents {
    return StaffContents(resultList)
}
