package io.github.droidkaigi.confsched2019.data.db

import io.github.droidkaigi.confsched2019.data.api.response.Response
import io.github.droidkaigi.confsched2019.data.db.entity.SessionWithSpeakers
import io.github.droidkaigi.confsched2019.data.db.entity.SpeakerEntity

interface SessionDatabase {
    suspend fun sessions(): List<SessionWithSpeakers>
    suspend fun allSpeaker(): List<SpeakerEntity>
    suspend fun save(apiResponse: Response)
}
