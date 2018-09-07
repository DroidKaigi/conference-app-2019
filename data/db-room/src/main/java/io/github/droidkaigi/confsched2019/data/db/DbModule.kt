package io.github.droidkaigi.confsched2019.data.db

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.data.db.entity.SessionDao

@Module(includes = [DbModule.Providers::class])
internal abstract class DbModule {
    @Binds
    abstract fun sessionDatabase(impl: RoomSessionDatabase): SessionDatabase

    @Module
    internal object Providers {
        @JvmStatic
        @Provides
        fun database(context: Context, filename: String?): SessionCacheDatabase {
            return Room.databaseBuilder(context, SessionCacheDatabase::class.java, filename ?: "droidkaigi.db")
                    .fallbackToDestructiveMigration()
                    .build()
        }

        @JvmStatic
        @Provides
        fun sessionDao(databaseSession: SessionCacheDatabase): SessionDao {
            return databaseSession.sessionDao()
        }
    }
}
