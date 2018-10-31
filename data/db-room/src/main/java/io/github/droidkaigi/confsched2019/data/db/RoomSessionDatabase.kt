package io.github.droidkaigi.confsched2019.data.db

import io.github.droidkaigi.confsched2019.data.api.response.Response
import io.github.droidkaigi.confsched2019.data.db.dao.SessionDao
import io.github.droidkaigi.confsched2019.data.db.dao.SessionSpeakerJoinDao
import io.github.droidkaigi.confsched2019.data.db.dao.SpeakerDao
import io.github.droidkaigi.confsched2019.data.db.entity.SessionWithSpeakers
import io.github.droidkaigi.confsched2019.data.db.entity.SpeakerEntity
import io.github.droidkaigi.confsched2019.data.db.entity.mapper.toSessionEntities
import io.github.droidkaigi.confsched2019.data.db.entity.mapper.toSessionSpeakerJoinEntities
import io.github.droidkaigi.confsched2019.data.db.entity.mapper.toSpeakerEntities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.reactive.openSubscription
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RoomSessionDatabase @Inject constructor(
    private val sessionDatabase: SessionCacheDatabase,
    private val sessionDao: SessionDao,
    private val speakerDao: SpeakerDao,
    private val sessionSpeakerJoinDao: SessionSpeakerJoinDao
) : SessionDatabase {
    override fun sessionsChannel(): ReceiveChannel<List<SessionWithSpeakers>> {
        return sessionSpeakerJoinDao.getAllSessionsObservable().openSubscription()
    }

    override suspend fun sessions(): List<SessionWithSpeakers> = withContext(Dispatchers.IO) {
        sessionSpeakerJoinDao.getAllSessions()
    }

    override suspend fun allSpeaker(): List<SpeakerEntity> = withContext(Dispatchers.IO) {
        speakerDao.getAllSpeaker()
    }

    override suspend fun save(apiResponse: Response) {
        withContext(Dispatchers.IO) {
            // FIXME: SQLiteDatabaseLockedException
            sessionDatabase.runInTransaction {
                sessionDatabase.sqlite().execSQL("PRAGMA defer_foreign_keys = TRUE")
                sessionDatabase.clearAllTables()
                val speakers = apiResponse.speakers.orEmpty().toSpeakerEntities()
                speakerDao.insert(speakers)
                val sessions = apiResponse.sessions
                val sessionEntities = sessions.toSessionEntities(apiResponse.categories.orEmpty(),
                    apiResponse.rooms.orEmpty())
                sessionDao.insert(sessionEntities)
                sessionSpeakerJoinDao.insert(sessions.toSessionSpeakerJoinEntities())
            }
        }
    }
}
