package io.github.droidkaigi.confsched2019.model

actual fun LocaleMessage.get() = if (defaultLang() != Lang.JA) enMessage else jaMessage
