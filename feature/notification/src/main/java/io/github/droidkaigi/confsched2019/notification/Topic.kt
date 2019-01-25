package io.github.droidkaigi.confsched2019.notification

sealed class Topic(val name: String) {
    object JaAnnouncement : Topic("jp_announcement")
    object EnAnnouncement : Topic("en_announcement")
}
