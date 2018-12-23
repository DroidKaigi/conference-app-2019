package io.github.droidkaigi.confsched2019.model

enum class Lang(val text: String) {
    EN("English"), JA("日本語");

    override fun toString(): String {
        return text
    }

    fun getString(en: String, ja: String): String {
        return if (this != JA) {
            en
        } else {
            ja
        }
    }
}

expect fun defaultLang(): Lang
