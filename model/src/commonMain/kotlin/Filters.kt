package io.github.droidkaigi.confsched2019.model

data class Filters(
    val rooms: MutableSet<Room> = mutableSetOf(),
    val topics: MutableSet<Topic> = mutableSetOf(),
    val langs: MutableSet<Lang> = mutableSetOf()
) {
    fun isPass(
        session: Session
    ): Boolean {
        if (session !is Session.SpeechSession) return true
        val roomFilterOk = run {
            if (rooms.isEmpty()) return@run true
            return@run rooms.contains(session.room)
        }
        val topicFilterOk = run {
            if (topics.isEmpty()) return@run true
            return@run topics.contains(session.topic)
        }
        val langFilterOk = run {
            if (langs.isEmpty()) return@run true
            return@run langs.map { it.toString() }.contains(session.language)
        }
        return roomFilterOk && topicFilterOk && langFilterOk
    }

    fun clear() {
        rooms.clear()
        topics.clear()
        langs.clear()
    }
}
