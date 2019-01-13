package io.github.droidkaigi.confsched2019.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.droidkaigi.confsched2019.data.db.entity.AnnouncementEntityImpl

@Dao
abstract class AnnouncementDao {
    @Query("SELECT * FROM announcement WHERE lang = :lang")
    abstract fun announcementsByLangLiveData(lang: String): LiveData<List<AnnouncementEntityImpl>>

    @Query("SELECT * FROM announcement WHERE lang = :lang")
    abstract fun announcementsByLang(lang: String): List<AnnouncementEntityImpl>

    @Query("DELETE FROM announcement")
    abstract fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(announcements: List<AnnouncementEntityImpl>)
}
