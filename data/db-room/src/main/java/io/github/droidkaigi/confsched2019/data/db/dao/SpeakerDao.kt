package io.github.droidkaigi.confsched2019.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.droidkaigi.confsched2019.data.db.entity.SpeakerEntityImpl

@Dao
abstract class SpeakerDao {
    @Query("SELECT * FROM speaker")
    abstract suspend fun getAllSpeaker(): List<SpeakerEntityImpl>

    @Query("DELETE FROM speaker")
    abstract fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(speakers: List<SpeakerEntityImpl>)
}
