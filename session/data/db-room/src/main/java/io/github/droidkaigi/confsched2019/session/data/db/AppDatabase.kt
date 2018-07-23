package io.github.droidkaigi.confsched2019.session.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.droidkaigi.confsched2019.session.data.db.entity.SessionDao
import io.github.droidkaigi.confsched2019.session.data.db.entity.SessionEntity

@Database(
        entities = [
            (SessionEntity::class)
        ],
        version = 9
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
}