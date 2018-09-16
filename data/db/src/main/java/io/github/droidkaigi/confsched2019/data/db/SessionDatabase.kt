package io.github.droidkaigi.confsched2019.data.db

import io.github.droidkaigi.confsched2019.data.api.response.Response
import io.github.droidkaigi.confsched2019.data.db.entity.SessionEntity
import kotlinx.coroutines.experimental.channels.ReceiveChannel

interface SessionDatabase {
    fun sessionsChannel(): ReceiveChannel<List<SessionEntity>>
    suspend fun sessions(): List<SessionEntity>
    suspend fun save(apiResponse: Response)
}
