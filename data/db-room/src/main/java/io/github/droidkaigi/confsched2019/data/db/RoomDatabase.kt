package io.github.droidkaigi.confsched2019.data.db

import io.github.droidkaigi.confsched2019.data.api.response.Response
import io.github.droidkaigi.confsched2019.data.api.response.SponsorResponse
import io.github.droidkaigi.confsched2019.data.db.dao.SessionDao
import io.github.droidkaigi.confsched2019.data.db.dao.SessionSpeakerJoinDao
import io.github.droidkaigi.confsched2019.data.db.dao.SpeakerDao
import io.github.droidkaigi.confsched2019.data.db.dao.SponsorDao
import io.github.droidkaigi.confsched2019.data.db.entity.SessionWithSpeakers
import io.github.droidkaigi.confsched2019.data.db.entity.SpeakerEntity
import io.github.droidkaigi.confsched2019.data.db.entity.SponsorEntity
import io.github.droidkaigi.confsched2019.data.db.entity.mapper.toSessionEntities
import io.github.droidkaigi.confsched2019.data.db.entity.mapper.toSessionSpeakerJoinEntities
import io.github.droidkaigi.confsched2019.data.db.entity.mapper.toSpeakerEntities
import io.github.droidkaigi.confsched2019.data.db.entity.mapper.toSponsorEntities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.reactive.openSubscription
import kotlinx.coroutines.withContext
import javax.inject.Inject

class
RoomDatabase @Inject constructor(
    private val database: CacheDatabase,
    private val sessionDao: SessionDao,
    private val speakerDao: SpeakerDao,
    private val sessionSpeakerJoinDao: SessionSpeakerJoinDao,
    private val sponsorDao: SponsorDao
) : SessionDatabase, SponsorDatabase {
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
            database.runInTransaction {
                database.sqlite().execSQL("PRAGMA defer_foreign_keys = TRUE")
                database.clearAllTables()
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

    override fun sponsorChannel(): ReceiveChannel<List<SponsorEntity>> {
        return sponsorDao.sponsorsFlowable().openSubscription()
    }

    override suspend fun save(apiResponse: SponsorResponse) {
        withContext(Dispatchers.IO) {
            database.runInTransaction {

                val sponsors = listOf(
                    apiResponse.platinum to "platinum",
                    apiResponse.gold to "gold",
                    apiResponse.support to "support",
                    apiResponse.tech to "tech"
                )
                    .mapIndexed { categoryIndex, (list, category) ->
                        list.toSponsorEntities(category, categoryIndex)
                    }
                    .flatten()

                sponsorDao.insert(sponsors)
            }
        }
    }
}
