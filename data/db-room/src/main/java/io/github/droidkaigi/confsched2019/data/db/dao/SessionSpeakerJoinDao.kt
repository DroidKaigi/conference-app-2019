package io.github.droidkaigi.confsched2019.data.db.dao

import androidx.annotation.CheckResult
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import io.github.droidkaigi.confsched2019.data.db.entity.SessionSpeakerJoinEntityImpl
import io.github.droidkaigi.confsched2019.data.db.entity.SessionWithSpeakersImpl
import org.intellij.lang.annotations.Language

@Dao
abstract class SessionSpeakerJoinDao {
    @Language("RoomSql")
    @Transaction
    @CheckResult
    @Query("SELECT * FROM session")
    abstract fun getAllSessions():
        List<SessionWithSpeakersImpl>

    @Insert abstract fun insert(sessionSpeakerJoin: List<SessionSpeakerJoinEntityImpl>)
}
