package io.github.droidkaigi.confsched2019.session.model

data class Topic(
        val id: Int,
        val name: String
) {

    fun getNameByLang(lang: Lang): String = name
            .split(" / ")
            .getOrElse(lang.ordinal, { name })
            .trim()
}
