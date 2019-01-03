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
import io.github.droidkaigi.confsched2019.model.SponsorCategory
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class RoomDatabase @Inject constructor(
    private val database: CacheDatabase,
    private val sessionDao: SessionDao,
    private val speakerDao: SpeakerDao,
    private val sessionSpeakerJoinDao: SessionSpeakerJoinDao,
    private val coroutineContext: CoroutineContext,
    private val sponsorDao: SponsorDao
) : SessionDatabase, SponsorDatabase  {
    override suspend fun sessions(): List<SessionWithSpeakers> {
        return sessionSpeakerJoinDao.getAllSessions()
    }

    override suspend fun allSpeaker(): List<SpeakerEntity> = speakerDao.getAllSpeaker()

    override suspend fun save(apiResponse: Response) {
        withContext(coroutineContext) {
            // FIXME: SQLiteDatabaseLockedException
            database.runInTransaction {
                database.sqlite().execSQL("PRAGMA defer_foreign_keys = TRUE")
                database.clearAllTables()
                val speakers = apiResponse.speakers.orEmpty().toSpeakerEntities()
                speakerDao.insert(speakers)
                val sessions = apiResponse.sessions
                val sessionEntities = sessions.toSessionEntities(
                    apiResponse.categories.orEmpty(),
                    apiResponse.rooms.orEmpty()
                )
                sessionDao.insert(sessionEntities)
                sessionSpeakerJoinDao.insert(sessions.toSessionSpeakerJoinEntities())
            }
        }
    }

    override suspend fun sponsors(): List<SponsorEntity> {
        return sponsorDao.allSponsors()
    }

    override suspend fun save(apiResponse: SponsorResponse) {
        withContext(coroutineContext) {
            database.runInTransaction {

                val sponsors = listOf(
                    SponsorCategory.Category.PLATINUM.id to apiResponse.platinum,
                    SponsorCategory.Category.GOLD.id to apiResponse.gold,
                    SponsorCategory.Category.SUPPORT.id to apiResponse.support,
                    SponsorCategory.Category.TECH.id to apiResponse.tech
                )
                    .mapIndexed { categoryIndex, (category, list) ->
                        list.toSponsorEntities(category, categoryIndex)
                    }
                    .flatten()

                sponsorDao.insert(sponsors)
            }
        }
    }
}
