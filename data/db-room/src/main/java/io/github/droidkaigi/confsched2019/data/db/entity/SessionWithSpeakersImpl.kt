package io.github.droidkaigi.confsched2019.data.db.entity

import androidx.room.Embedded
import androidx.room.Relation

data class SessionWithSpeakersImpl(
        @Embedded override var session: SessionEntity? = null,
        @Relation(
                parentColumn = "id",
                entityColumn = "sessionId",
                projection = ["speakerId"],
                entity = SessionSpeakerJoinEntityImpl::class)
        override var speakerIdList: List<String> = emptyList()
) : SessionWithSpeakers
