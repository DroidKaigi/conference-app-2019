package io.github.droidkaigi.confsched2019.notification

sealed class Topic(val name: String) {
    object JpAnnouncement : Topic("jp/announcement")
    object EnAnnouncement : Topic("en/announcement")
}
