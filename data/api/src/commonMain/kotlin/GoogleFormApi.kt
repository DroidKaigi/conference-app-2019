package io.github.droidkaigi.confsched2019.data.api

import io.github.droidkaigi.confsched2019.model.LocaledString

interface GoogleFormApi {
    suspend fun submitSessionFeedback(
        sessionId: String,
        sessionTitle: LocaledString,
        totalEvaluation: Int,
        relevancy: Int,
        asExpected: Int,
        difficulty: Int,
        knowledgeable: Int,
        comment: String
    ): String
}
