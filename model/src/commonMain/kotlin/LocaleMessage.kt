package io.github.droidkaigi.confsched2019.model

class LocaleMessage(val enMessage: String, val jaMessage: String)

expect fun LocaleMessage.get(): String
