package io.github.droidkaigi.confsched2019.model

enum class SessionType(
    val id: String,
    val isFavoritable: Boolean,
    val supportDetail: Boolean,
    val isFilterable: Boolean
) {
    NORMAL(
        id = "normal",
        isFavoritable = false,
        supportDetail = true,
        isFilterable = false),
    WELCOME_TALK(
        id = "welcome_talk",
        isFavoritable = false,
        supportDetail = false,
        isFilterable = false),
    RESERVED(
        id = "reserved",
        isFavoritable = false,
        supportDetail = false,
        isFilterable = false),
    CODELABS(
        id = "codelabs",
        isFavoritable = true,
        supportDetail = true,
        isFilterable = true),
    FIRESIDE_CHAT(
        id = "fireside_chat",
        isFavoritable = false,
        supportDetail = true,
        isFilterable = false),
    LUNCH(
        id = "lunch",
        isFavoritable = false,
        supportDetail = false,
        isFilterable = false),
    BREAKTIME(
        id = "break",
        isFavoritable = false,
        supportDetail = false,
        isFilterable = false),
    AFTER_PARTY(
        id = "after_party",
        isFavoritable = false,
        supportDetail = true,
        isFilterable = false),
    UNKNOWN(
        id = "unknown",
        isFavoritable = false,
        supportDetail = false,
        isFilterable = false);

    companion object {
        fun of(id: String?) = values().find { it.id == id } ?: UNKNOWN
    }
}
