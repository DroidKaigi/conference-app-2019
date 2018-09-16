package io.github.droidkaigi.confsched2019.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.droidkaigi.confsched2019.data.db.entity.SessionDao
import io.github.droidkaigi.confsched2019.data.db.entity.SessionEntityImpl

@Database(
        entities = [
            (SessionEntityImpl::class)
        ],
        version = 1
)
@TypeConverters(Converters::class)
abstract class SessionCacheDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
}
