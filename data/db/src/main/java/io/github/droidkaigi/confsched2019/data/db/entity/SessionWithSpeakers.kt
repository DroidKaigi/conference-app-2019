package io.github.droidkaigi.confsched2019.data.db.entity

interface SessionWithSpeakers {
    val session: SessionEntity
    val speakerIdList: List<String>
}
