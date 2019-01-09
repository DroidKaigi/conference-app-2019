package io.github.droidkaigi.confsched2019.model

class LocaleMessage(val enMessage: String, val jaMessage: String)

fun LocaleMessage.get() = if (defaultLang() != Lang.JA) enMessage else jaMessage
