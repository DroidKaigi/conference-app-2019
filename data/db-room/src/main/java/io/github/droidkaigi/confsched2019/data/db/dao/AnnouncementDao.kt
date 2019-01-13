package io.github.droidkaigi.confsched2019.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.droidkaigi.confsched2019.data.db.entity.AnnouncementEntityImpl

@Dao
abstract class AnnouncementDao {
    @Query("SELECT * FROM announcement WHERE lower(lang) = lower(:lang)")
    abstract suspend fun announcementsByLang(lang: String): List<AnnouncementEntityImpl>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(announcements: List<AnnouncementEntityImpl>)
}
