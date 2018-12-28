package io.github.droidkaigi.confsched2019.data.api

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.url
import io.ktor.http.parametersOf
import javax.inject.Inject

class KtorGoogleFormApi @Inject constructor(
    val httpClient: HttpClient
) : GoogleFormApi {

    override suspend fun submitSessionFeedback(
        sessionId: String,
        sessionTitle: String,
        totalEvaluation: Int,
        relevancy: Int,
        asExpected: Int,
        difficulty: Int,
        knowledgeable: Int,
        comment: String
    ): String {
        return httpClient.submitForm(
            formData = parametersOf(
                "entry.587733619" to listOf(sessionId),
                "entry.893997240" to listOf(sessionTitle),
                "entry.2026763339" to listOf(totalEvaluation.toString()),
                "entry.499312343" to listOf(relevancy.toString()),
                "entry.715223183" to listOf(asExpected.toString()),
                "entry.851615750" to listOf(difficulty.toString()),
                "entry.1371898664" to listOf(knowledgeable.toString()),
                "entry.1819923854" to listOf(comment)
            )
        ) {
            url("https://docs.google.com/forms/d/e/1FAIpQLScM2l7A2fRI1bidNKsswkHO6CIJ_bqwiWmncjyTRLP9JyPnUA/formResponse")
        }
    }
}
