package io.github.droidkaigi.confsched2019.model

enum class SessionType(val id: String, val isFavoritable: Boolean, val isFilterable: Boolean) {
    Normal(id = "normal", isFavoritable = false, isFilterable = false),
    WelcomeTalk(id = "welcome_talk", isFavoritable = false, isFilterable = false),
    Reserved(id = "reserved", isFavoritable = false, isFilterable = false),
    Codelabs(id = "codelabs", isFavoritable = false, isFilterable = true),
    FiresideChat(id = "fireside_chat", isFavoritable = false, isFilterable = false),
    Lunch(id = "lunch", isFavoritable = false, isFilterable = false),
    Break(id = "break", isFavoritable = false, isFilterable = false),
    AfterParty(id = "after_party", isFavoritable = false, isFilterable = false),
    Unknown(id = "unknown", isFavoritable = false, isFilterable = false);

    companion object {
        fun of(id: String?) = values().find { it.id == id } ?: Unknown
    }
}
