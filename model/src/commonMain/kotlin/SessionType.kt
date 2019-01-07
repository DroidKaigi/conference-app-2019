package io.github.droidkaigi.confsched2019.model

enum class SessionType(val id: String) {
    Normal("normal"),
    WelcomeTalk("welcome_talk"),
    Reserved("reserved"),
    Codelabs("codelabs"),
    FiresideChat("fireside_chat"),
    Lunch("fireside_chat"),
    Break("fireside_chat"),
    AfterParty("fireside_chat"),
    Unknown("unknown");

    companion object {
        fun of(id: String?) = values().find { it.id == id } ?: Unknown
    }
}
