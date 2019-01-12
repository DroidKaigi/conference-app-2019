package io.github.droidkaigi.confsched2019.model

enum class SessionType(val id: String, val isFavoritable: Boolean) {
    NORMAL("normal", false),
    WELCOME_TALK("welcome_talk", false),
    RESERVED("reserved", false),
    CODELABS("codelabs", true),
    FIRESIDE_CHAT("fireside_chat", false),
    LUNCH("lunch", false),
    BREAK("break", false),
    AFTER_PARTY("after_party", false),
    UNKNOWN("unknown", false);

    companion object {
        fun of(id: String?) = values().find { it.id == id } ?: UNKNOWN
    }
}
