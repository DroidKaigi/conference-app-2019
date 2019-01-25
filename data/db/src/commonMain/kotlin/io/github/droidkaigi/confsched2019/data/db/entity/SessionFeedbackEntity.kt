package io.github.droidkaigi.confsched2019.data.db.entity

interface SessionFeedbackEntity {
    val sessionId: String
    val totalEvaluation: Int
    val relevancy: Int
    val asExpected: Int
    val difficulty: Int
    val knowledgeable: Int
    val comment: String
    val submitted: Boolean
}
