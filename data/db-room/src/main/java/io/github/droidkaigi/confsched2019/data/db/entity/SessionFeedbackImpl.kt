package io.github.droidkaigi.confsched2019.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "session_feedback")
data class SessionFeedbackImpl(
    @PrimaryKey @ColumnInfo(name = "session_id", index = true) override var sessionId: String,
    @ColumnInfo(name = "total_evaluation") override val totalEvaluation: Int,
    override val relevancy: Int,
    override val asExpected: Int,
    override val difficulty: Int,
    override val knowledgeable: Int,
    override val comment: String,
    override val submitted: Boolean
) : SessionFeedback
