package io.github.droidkaigi.confsched2019.data.db

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.data.db.dao.AnnouncementDao
import io.github.droidkaigi.confsched2019.data.db.dao.SessionDao
import io.github.droidkaigi.confsched2019.data.db.dao.SessionFeedbackDao
import io.github.droidkaigi.confsched2019.data.db.dao.SessionSpeakerJoinDao
import io.github.droidkaigi.confsched2019.data.db.dao.SpeakerDao
import io.github.droidkaigi.confsched2019.data.db.dao.SponsorDao
import io.github.droidkaigi.confsched2019.data.db.dao.StaffDao
import javax.inject.Singleton

@Module(includes = [DbModule.Providers::class])
internal abstract class DbModule {
    @Binds abstract fun sessionDatabase(impl: RoomDatabase): SessionDatabase
    @Binds abstract fun sponsorDatabase(impl: RoomDatabase): SponsorDatabase
    @Binds abstract fun announcementDatabase(impl: RoomDatabase): AnnouncementDatabase
    @Binds abstract fun staffDatabase(impl: RoomDatabase): StaffDatabase

    @Module
    internal object Providers {
        @Singleton @JvmStatic @Provides fun database(
            context: Context,
            filename: String?
        ): CacheDatabase {
            return Room.databaseBuilder(
                context,
                CacheDatabase::class.java,
                filename ?: "droidkaigi.db"
            )
                .fallbackToDestructiveMigration()
                .build()
        }

        @JvmStatic @Provides fun sessionDao(database: CacheDatabase): SessionDao {
            return database.sessionDao()
        }

        @JvmStatic @Provides fun speakerDao(database: CacheDatabase): SpeakerDao {
            return database.speakerDao()
        }

        @JvmStatic @Provides fun sessionSpeakerJoinDao(
            database: CacheDatabase
        ): SessionSpeakerJoinDao {
            return database.sessionSpeakerJoinDao()
        }

        @JvmStatic @Provides fun sponsorDao(databaseSponsor: CacheDatabase): SponsorDao {
            return databaseSponsor.sponsorDao()
        }

        @JvmStatic @Provides fun announcementDao(database: CacheDatabase): AnnouncementDao {
            return database.announcementDao()
        }

        @JvmStatic @Provides fun staffDao(database: CacheDatabase): StaffDao {
            return database.staffDao()
        }

        @JvmStatic @Provides fun sessionFeedbackDao(database: CacheDatabase): SessionFeedbackDao {
            return database.sessionFeedbackDao()
        }
    }
}
