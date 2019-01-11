package io.github.droidkaigi.confsched2019.model

enum class SessionType(val id: String, val isFavoritable: Boolean) {
    Normal("normal", false),
    WelcomeTalk("welcome_talk", false),
    Reserved("reserved", false),
    Codelabs("codelabs", true),
    FiresideChat("fireside_chat", false),
    Lunch("lunch", false),
    Break("break", false),
    AfterParty("after_party", false),
    Unknown("unknown", false);

    companion object {
        fun of(id: String?) = values().find { it.id == id } ?: Unknown
    }
}
