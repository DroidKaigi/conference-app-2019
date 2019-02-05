package io.github.droidkaigi.confsched2019.model

enum class SessionType(
    val id: String,
    val isFavoritable: Boolean,
    val supportDetail: Boolean,
    val isFilterable: Boolean,
    val shouldShowRoom: Boolean
) {
    NORMAL(
        id = "normal",
        isFavoritable = false,
        supportDetail = true,
        isFilterable = false,
        shouldShowRoom = true
    ),
    WELCOME_TALK(
        id = "welcome_talk",
        isFavoritable = false,
        supportDetail = false,
        isFilterable = false,
        shouldShowRoom = true
    ),
    RESERVED(
        id = "reserved",
        isFavoritable = false,
        supportDetail = false,
        isFilterable = false,
        shouldShowRoom = true
    ),
    CODELABS(
        id = "codelabs",
        isFavoritable = true,
        supportDetail = true,
        isFilterable = true,
        shouldShowRoom = true
    ),
    FIRESIDE_CHAT(
        id = "fireside_chat",
        isFavoritable = false,
        supportDetail = true,
        isFilterable = false,
        shouldShowRoom = true
    ),
    LUNCH(
        id = "lunch",
        isFavoritable = false,
        supportDetail = false,
        isFilterable = false,
        shouldShowRoom = false
    ),
    BREAKTIME(
        id = "break",
        isFavoritable = false,
        supportDetail = false,
        isFilterable = false,
        shouldShowRoom = true
    ),
    AFTER_PARTY(
        id = "after_party",
        isFavoritable = false,
        supportDetail = true,
        isFilterable = false,
        shouldShowRoom = true
    ),
    UNKNOWN(
        id = "unknown",
        isFavoritable = false,
        supportDetail = false,
        isFilterable = false,
        shouldShowRoom = true
    );

    companion object {
        fun of(id: String?) = values().find { it.id == id } ?: UNKNOWN
    }
}
