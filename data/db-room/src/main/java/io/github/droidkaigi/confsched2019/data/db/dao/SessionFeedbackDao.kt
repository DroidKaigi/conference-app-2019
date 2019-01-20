package io.github.droidkaigi.confsched2019.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.droidkaigi.confsched2019.data.db.entity.SessionFeedbackEntityImpl

@Dao
abstract class SessionFeedbackDao {
    @Query(
        "SELECT session_feedback.*, session.title as session_title " +
            "FROM session_feedback INNER JOIN session ON session.id = session_feedback.session_id"
    )
    abstract fun sessionFeedbacksLiveData(): LiveData<List<SessionFeedbackEntityImpl>>

    @Query(
        "SELECT session_feedback.*, session.title as session_title " +
            "FROM session_feedback INNER JOIN session ON session.id = session_feedback.session_id"
    )
    abstract fun sessionFeedbacks(): List<SessionFeedbackEntityImpl>

    @Query("DELETE FROM session_feedback WHERE session_id = :sessionId")
    abstract fun delete(sessionId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun upsert(sessionFeedback: SessionFeedbackEntityImpl)
}
