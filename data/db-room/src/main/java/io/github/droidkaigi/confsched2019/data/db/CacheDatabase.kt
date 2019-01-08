package io.github.droidkaigi.confsched2019.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.droidkaigi.confsched2019.data.db.dao.SessionDao
import io.github.droidkaigi.confsched2019.data.db.dao.SessionFeedbackDao
import io.github.droidkaigi.confsched2019.data.db.dao.SessionSpeakerJoinDao
import io.github.droidkaigi.confsched2019.data.db.dao.SpeakerDao
import io.github.droidkaigi.confsched2019.data.db.dao.SponsorDao
import io.github.droidkaigi.confsched2019.data.db.entity.SessionEntityImpl
import io.github.droidkaigi.confsched2019.data.db.entity.SessionFeedbackImpl
import io.github.droidkaigi.confsched2019.data.db.entity.SessionSpeakerJoinEntityImpl
import io.github.droidkaigi.confsched2019.data.db.entity.SpeakerEntityImpl
import io.github.droidkaigi.confsched2019.data.db.entity.SponsorEntityImpl

@Database(
    entities = [
        (SessionEntityImpl::class),
        (SpeakerEntityImpl::class),
        (SessionSpeakerJoinEntityImpl::class),
        (SponsorEntityImpl::class),
        (SessionFeedbackImpl::class)
    ],
    version = 8
)
abstract class CacheDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun speakerDao(): SpeakerDao
    abstract fun sessionSpeakerJoinDao(): SessionSpeakerJoinDao
    abstract fun sponsorDao(): SponsorDao
    abstract fun sessionFeedbackDao(): SessionFeedbackDao
    fun sqlite(): SupportSQLiteDatabase {
        return mDatabase
    }
}
