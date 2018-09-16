package io.github.droidkaigi.confsched2019.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import io.github.droidkaigi.confsched2019.data.db.entity.SessionSpeakerJoinEntityImpl

@Dao
abstract class SessionSpeakerJoinDao {
//    @Language("RoomSql")
//    @Transaction
//    @CheckResult
//    @Query("SELECT * FROM session")
//    abstract fun getAllSessions(): Flowable<List<SessionWithSpeakers>>

    @Insert
    abstract fun insert(sessionSpeakerJoin: List<SessionSpeakerJoinEntityImpl>)
}
