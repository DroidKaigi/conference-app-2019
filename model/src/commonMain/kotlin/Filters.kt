package io.github.droidkaigi.confsched2019.model

data class Filters(
    val rooms: Set<Room> = mutableSetOf(),
    val categories: Set<Category> = mutableSetOf(),
    val langs: Set<Lang> = mutableSetOf()
) {
    fun isPass(
        session: Session
    ): Boolean {
        if (session !is Session.SpeechSession) return true
        val roomFilterOk = run {
            if (rooms.isEmpty()) return@run true
            return@run rooms.contains(session.room)
        }
        val categoryFilterOk = run {
            if (categories.isEmpty()) return@run true
            return@run categories.contains(session.category)
        }
        val langFilterOk = run {
            if (langs.isEmpty()) return@run true
            return@run langs.map { it.text }.contains(session.language)
        }
        return roomFilterOk && categoryFilterOk && langFilterOk
    }
}
