package io.github.droidkaigi.confsched2019.model

enum class SessionType(val id: String, val isFavoritable: Boolean, val supportDetail: Boolean) {
    Normal(
        id = "normal",
        isFavoritable = false,
        supportDetail = true),
    WelcomeTalk(
        id = "welcome_talk",
        isFavoritable = false,
        supportDetail = false),
    Reserved(
        id = "reserved",
        isFavoritable = false,
        supportDetail = false),
    Codelabs(
        id = "codelabs",
        isFavoritable = true,
        supportDetail = true),
    FiresideChat(
        id = "fireside_chat",
        isFavoritable = false,
        supportDetail = true),
    Lunch(
        id = "lunch",
        isFavoritable = false,
        supportDetail = false),
    Break(
        id = "break",
        isFavoritable = false,
        supportDetail = false),
    AfterParty(
        id = "after_party",
        isFavoritable = false,
        supportDetail = true),
    Unknown(
        id = "unknown",
        isFavoritable = false,
        supportDetail = false);

    companion object {
        fun of(id: String?) = values().find { it.id == id } ?: Unknown
    }
}
