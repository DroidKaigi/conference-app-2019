package io.github.droidkaigi.confsched2019.model

data class SessionMessage(
    val jaMessage: String,
    val enMessage: String
) {

    fun getBodyByLang(lang: Lang): String = if (lang == Lang.JA) {
        jaMessage
    } else {
        enMessage
    }
}
