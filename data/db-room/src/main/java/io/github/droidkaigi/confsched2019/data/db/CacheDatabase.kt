package io.github.droidkaigi.confsched2019.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.droidkaigi.confsched2019.data.db.dao.AnnouncementDao
import io.github.droidkaigi.confsched2019.data.db.dao.ContributorDao
import io.github.droidkaigi.confsched2019.data.db.dao.SessionDao
import io.github.droidkaigi.confsched2019.data.db.dao.SessionFeedbackDao
import io.github.droidkaigi.confsched2019.data.db.dao.SessionSpeakerJoinDao
import io.github.droidkaigi.confsched2019.data.db.dao.SpeakerDao
import io.github.droidkaigi.confsched2019.data.db.dao.SponsorDao
import io.github.droidkaigi.confsched2019.data.db.dao.StaffDao
import io.github.droidkaigi.confsched2019.data.db.entity.AnnouncementEntityImpl
import io.github.droidkaigi.confsched2019.data.db.entity.ContributorEntityImpl
import io.github.droidkaigi.confsched2019.data.db.entity.SessionEntityImpl
import io.github.droidkaigi.confsched2019.data.db.entity.SessionFeedbackEntityImpl
import io.github.droidkaigi.confsched2019.data.db.entity.SessionSpeakerJoinEntityImpl
import io.github.droidkaigi.confsched2019.data.db.entity.SpeakerEntityImpl
import io.github.droidkaigi.confsched2019.data.db.entity.SponsorEntityImpl
import io.github.droidkaigi.confsched2019.data.db.entity.StaffEntityImpl

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
    version = 13
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
