package io.github.droidkaigi.confsched2019.model

data class Filters(
    val rooms: Set<Room> = mutableSetOf(),
    val categories: Set<Category> = mutableSetOf(),
    val langs: Set<Lang> = mutableSetOf(),
    val langSupports: Set<LangSupport> = mutableSetOf(),
    val audienceCategories: Set<AudienceCategory> = mutableSetOf()
) {
    fun isPass(
        session: Session
    ): Boolean {
        if (session.isNotFilterableServiceSession()) return true
        val roomFilterOk = run {
            if (rooms.isEmpty()) return@run true
            return@run rooms.contains(session.room)
        }
        if (session !is SpeechSession) return roomFilterOk
        val categoryFilterOk = run {
            if (categories.isEmpty()) return@run true
            return@run categories.contains(session.category)
        }
        val langFilterOk = run {
            if (langs.isEmpty()) return@run true
            return@run langs.any { it == session.lang }
        }
        val langSupportFilterOk = run {
            if (langSupports.contains(LangSupport.INTERPRETATION)) {
                session.isInterpretationTarget
            } else {
                true
            }
        }
        val audienceCategoryFilterOk = run {
            if (audienceCategories.isEmpty()) return@run true
            val audienceCategory = if (session.forBeginners) {
                AudienceCategory.BEGINNERS
            } else {
                AudienceCategory.UNSPECIFIED
            }
            audienceCategories.any { it == audienceCategory }
        }
        return roomFilterOk &&
            categoryFilterOk &&
            langFilterOk &&
            langSupportFilterOk &&
            audienceCategoryFilterOk
    }

    fun isFiltered(): Boolean {
        return rooms.isNotEmpty() ||
            categories.isNotEmpty() ||
            langs.isNotEmpty() ||
            langSupports.isNotEmpty() ||
            audienceCategories.isNotEmpty()
    }

    private fun Session.isNotFilterableServiceSession() =
        this is ServiceSession && !sessionType.isFilterable
}
