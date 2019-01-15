package io.github.droidkaigi.confsched2019.model

enum class AudienceCategory(val text: LocaledString) {
    BEGINNERS(LocaledString("初心者歓迎", "Beginners")),
    UNSPECIFIED(LocaledString("指定無し", "Unspecified"))
}
