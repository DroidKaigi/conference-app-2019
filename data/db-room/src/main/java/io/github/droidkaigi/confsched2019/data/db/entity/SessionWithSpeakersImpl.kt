package io.github.droidkaigi.confsched2019.data.db.entity

import androidx.room.Embedded
import androidx.room.Relation

data class SessionWithSpeakersImpl(
    @Embedded override val session: SessionEntityImpl
) : SessionWithSpeakers {
    @Relation(
        parentColumn = "id",
        entityColumn = "sessionId",
        projection = ["speakerId"],
        entity = SessionSpeakerJoinEntityImpl::class
    )
    override var speakerIdList: List<String> = emptyList()
}
