package io.github.droidkaigi.confsched2019.model

enum class SessionType(val id: String) {
    Normal("normal"),
    WelcomeTalk("welcome_talk"),
    Reserved("reserved"),
    Codelabs("codelabs"),
    FiresideChat("fireside_chat"),
    Lunch("lunch"),
    Break("break"),
    AfterParty("after_party"),
    Unknown("unknown");

    companion object {
        fun of(id: String?) = values().find { it.id == id } ?: Unknown
    }
}
