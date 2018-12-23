package io.github.droidkaigi.confsched2019.model

import java.util.Locale

actual fun defaultLang() = if (Locale.getDefault() == Locale.JAPAN) Lang.JA else Lang.EN
