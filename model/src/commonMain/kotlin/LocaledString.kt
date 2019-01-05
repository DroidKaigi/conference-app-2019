package io.github.droidkaigi.confsched2019.model

data class LocaledString(
    val ja: String,
    val en: String
) {
    fun getByLang(lang: Lang): String {
        return if (lang == Lang.JA) {
            ja
        } else {
            en
        }
    }
}
