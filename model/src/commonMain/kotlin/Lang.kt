package io.github.droidkaigi.confsched2019.model

enum class Lang(val text: String) {
    JA("日本語"), EN("English");

    override fun toString(): String {
        return text
    }
}
