package io.github.droidkaigi.confsched2019.model

data class Filters(
    val rooms: Set<Room> = mutableSetOf(),
    val categories: Set<Category> = mutableSetOf(),
    val langs: Set<Lang> = mutableSetOf(),
    val langSupports: Set<LangSupport> = mutableSetOf()
) {
    fun isPass(
        session: Session
    ): Boolean {
        if (session.isNotFilterableServiceSession()) return true
        val roomFilterOk = run {
            if (rooms.isEmpty()) return@run true
            return@run rooms.contains(session.room)
        }
        if (session !is Session.SpeechSession) return roomFilterOk
        val categoryFilterOk = run {
            if (categories.isEmpty()) return@run true
            return@run categories.contains(session.category)
        }
        val langFilterOk = run {
            if (langs.isEmpty()) return@run true
            return@run langs.map { it.text }.contains(session.language)
        }
        val langSupportFilterOk = run {
            return@run if (langSupports.contains(LangSupport.INTERPRETATION)) session.isInterpretationTarget else true
        }
        return roomFilterOk && categoryFilterOk && langFilterOk && langSupportFilterOk
    }

    fun isFiltered(): Boolean {
        return rooms.isNotEmpty() || categories.isNotEmpty() || langs.isNotEmpty() || langSupports.isNotEmpty()
    }

    private fun Session.isNotFilterableServiceSession()
        = this is Session.ServiceSession && !sessionType.isFilterable
}
