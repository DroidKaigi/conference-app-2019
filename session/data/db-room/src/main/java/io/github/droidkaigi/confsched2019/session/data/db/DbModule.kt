package io.github.droidkaigi.confsched2019.session.data.db

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.session.data.db.entity.SessionDao

@Module(includes = [DbModule.Providers::class])
internal abstract class DbModule {
    @Binds
    abstract fun sessionDatabase(impl: RoomSessionDatabase): SessionDatabase

    @Module
    internal object Providers {
        @JvmStatic
        @Provides
        fun database(context: Context, filename: String?): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, filename ?: "droidkaigi.db")
                    .fallbackToDestructiveMigration()
                    .build()
        }

        @JvmStatic
        @Provides
        fun sessionDao(database: AppDatabase): SessionDao {
            return database.sessionDao()
        }
    }
}
