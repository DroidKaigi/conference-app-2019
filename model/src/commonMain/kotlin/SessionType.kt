package io.github.droidkaigi.confsched2019.model

enum class SessionType(val id: String, val isFavoritable: Boolean, val isFilterable: Boolean) {
    NORMAL(id = "normal", isFavoritable = false, isFilterable = false),
    WELCOME_TALK(id = "welcome_talk", isFavoritable = false, isFilterable = false),
    RESERVED(id = "reserved", isFavoritable = false, isFilterable = false),
    CODELABS(id = "codelabs", isFavoritable = false, isFilterable = true),
    FIRESIDE_CHAT(id = "fireside_chat", isFavoritable = false, isFilterable = false),
    LUNCH(id = "lunch", isFavoritable = false, isFilterable = false),
    BREAKTIME(id = "break", isFavoritable = false, isFilterable = false),
    AFTER_PARTY(id = "after_party", isFavoritable = false, isFilterable = false),
    UNKNOWN(id = "unknown", isFavoritable = false, isFilterable = false);

    companion object {
        fun of(id: String?) = values().find { it.id == id } ?: UNKNOWN
    }
}
