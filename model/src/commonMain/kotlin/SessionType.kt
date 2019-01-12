package io.github.droidkaigi.confsched2019.model

enum class SessionType(val id: String, val isFavoritable: Boolean, val isFilterable: Boolean) {
    Normal("normal", false, false),
    WelcomeTalk("welcome_talk", false, false),
    Reserved("reserved", false, false),
    Codelabs("codelabs", true, true),
    FiresideChat("fireside_chat", false, false),
    Lunch("lunch", false, false),
    Break("break", false, false),
    AfterParty("after_party", false, false),
    Unknown("unknown", false, false);

    companion object {
        fun of(id: String?) = values().find { it.id == id } ?: Unknown
    }
}
