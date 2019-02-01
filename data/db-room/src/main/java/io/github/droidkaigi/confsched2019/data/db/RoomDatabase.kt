package io.github.droidkaigi.confsched2019.data.db

import io.github.droidkaigi.confsched2019.data.api.response.AnnouncementResponse
import io.github.droidkaigi.confsched2019.data.api.response.ContributorResponse
import io.github.droidkaigi.confsched2019.data.api.response.Response
import io.github.droidkaigi.confsched2019.data.api.response.SponsorResponse
import io.github.droidkaigi.confsched2019.data.api.response.StaffResponse
import io.github.droidkaigi.confsched2019.data.db.dao.AnnouncementDao
import io.github.droidkaigi.confsched2019.data.db.dao.ContributorDao
import io.github.droidkaigi.confsched2019.data.db.dao.SessionDao
import io.github.droidkaigi.confsched2019.data.db.dao.SessionFeedbackDao
import io.github.droidkaigi.confsched2019.data.db.dao.SessionSpeakerJoinDao
import io.github.droidkaigi.confsched2019.data.db.dao.SpeakerDao
import io.github.droidkaigi.confsched2019.data.db.dao.SponsorDao
import io.github.droidkaigi.confsched2019.data.db.dao.StaffDao
import io.github.droidkaigi.confsched2019.data.db.entity.AnnouncementEntity
import io.github.droidkaigi.confsched2019.data.db.entity.ContributorEntity
import io.github.droidkaigi.confsched2019.data.db.entity.SessionFeedbackEntity
import io.github.droidkaigi.confsched2019.data.db.entity.SessionWithSpeakers
import io.github.droidkaigi.confsched2019.data.db.entity.SpeakerEntity
import io.github.droidkaigi.confsched2019.data.db.entity.SponsorEntity
import io.github.droidkaigi.confsched2019.data.db.entity.StaffEntity
import io.github.droidkaigi.confsched2019.data.db.entity.mapper.toAnnouncementEntities
import io.github.droidkaigi.confsched2019.data.db.entity.mapper.toContributorEntities
import io.github.droidkaigi.confsched2019.data.db.entity.mapper.toSessionEntities
import io.github.droidkaigi.confsched2019.data.db.entity.mapper.toSessionFeedbackEntity
import io.github.droidkaigi.confsched2019.data.db.entity.mapper.toSessionSpeakerJoinEntities
import io.github.droidkaigi.confsched2019.data.db.entity.mapper.toSpeakerEntities
import io.github.droidkaigi.confsched2019.data.db.entity.mapper.toSponsorEntities
import io.github.droidkaigi.confsched2019.data.db.entity.mapper.toStaffEntities
import io.github.droidkaigi.confsched2019.model.SessionFeedback
import io.github.droidkaigi.confsched2019.model.SponsorCategory
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class RoomDatabase @Inject constructor(
    private val database: CacheDatabase,
    private val sessionDao: SessionDao,
    private val speakerDao: SpeakerDao,
    private val sessionSpeakerJoinDao: SessionSpeakerJoinDao,
    private val sessionFeedbackDao: SessionFeedbackDao,
    private val coroutineContext: CoroutineContext,
    private val sponsorDao: SponsorDao,
    private val announcementDao: AnnouncementDao,
    private val staffDao: StaffDao,
    private val contributorDao: ContributorDao
) : SessionDatabase, SponsorDatabase, AnnouncementDatabase, StaffDatabase, ContributorDatabase {
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

    override suspend fun sessionFeedbacks(): List<SessionFeedbackEntity> {
        return sessionFeedbackDao.sessionFeedbacks()
    }

    override suspend fun saveSessionFeedback(sessionFeedback: SessionFeedback) {
        sessionFeedbackDao.upsert(sessionFeedback.toSessionFeedbackEntity())
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

    override suspend fun announcementsByLang(lang: String): List<AnnouncementEntity> =
        announcementDao.announcementsByLang(lang)

    override suspend fun save(apiResponse: List<AnnouncementResponse>) {
        withContext(coroutineContext) {
            database.runInTransaction {
                val announcements = apiResponse.toAnnouncementEntities()

                announcementDao.insert(announcements)
            }
        }
    }

    override suspend fun staffs(): List<StaffEntity> = staffDao.allStaffs()

    override suspend fun save(apiResponse: StaffResponse) {
        withContext(coroutineContext) {
            database.runInTransaction {
                val staffs = apiResponse.staffs.toStaffEntities()
                staffDao.deleteAll()
                staffDao.insert(staffs)
            }
        }
    }

    override suspend fun contributorList(): List<ContributorEntity> =
        contributorDao.allContributors()

    override suspend fun save(apiResponse: ContributorResponse) {
        withContext(coroutineContext) {
            database.runInTransaction {
                val contributors = apiResponse.contributors.toContributorEntities()
                contributorDao.insert(contributors)
            }
        }
    }
}
