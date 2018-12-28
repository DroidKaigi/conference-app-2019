package io.github.droidkaigi.confsched2019.data.api

interface GoogleFormApi {
    suspend fun submitSessionFeedback(
        sessionId: String,
        sessionTitle: String,
        totalEvaluation: Int,
        relevancy: Int,
        asExpected: Int,
        difficulty: Int,
        knowledgeable: Int,
        comment: String
    ): String
}
