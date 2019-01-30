package io.github.droidkaigi.confsched2019.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.droidkaigi.confsched2019.data.db.dao.*
import io.github.droidkaigi.confsched2019.data.db.entity.*

@Database(
    entities = [
        (SessionEntityImpl::class),
        (SpeakerEntityImpl::class),
        (SessionSpeakerJoinEntityImpl::class),
        (SponsorEntityImpl::class),
        (SessionFeedbackEntityImpl::class),
        (AnnouncementEntityImpl::class),
        (StaffEntityImpl::class),
        (ContributorEntityImpl::class)
    ],
    version = 14
)
abstract class CacheDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun speakerDao(): SpeakerDao
    abstract fun sessionSpeakerJoinDao(): SessionSpeakerJoinDao
    abstract fun sponsorDao(): SponsorDao
    abstract fun sessionFeedbackDao(): SessionFeedbackDao
    abstract fun announcementDao(): AnnouncementDao
    abstract fun staffDao(): StaffDao
    abstract fun contributorDao(): ContributorDao
    fun sqlite(): SupportSQLiteDatabase {
        return mDatabase
    }
}
