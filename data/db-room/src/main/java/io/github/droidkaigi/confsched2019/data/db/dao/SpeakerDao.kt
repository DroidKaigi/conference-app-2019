package io.github.droidkaigi.confsched2019.data.db.dao

import androidx.room.*
import io.github.droidkaigi.confsched2019.data.db.entity.SpeakerEntityImpl

@Dao
abstract class SpeakerDao {
    @Query("SELECT * FROM speaker")
    abstract fun getAllSpeaker(): List<SpeakerEntityImpl>

    @Query("DELETE FROM speaker")
    abstract fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(speakers: List<SpeakerEntityImpl>)

    @Transaction
    open fun clearAndInsert(newSessions: List<SpeakerEntityImpl>) {
        deleteAll()
        insert(newSessions)
    }
}
