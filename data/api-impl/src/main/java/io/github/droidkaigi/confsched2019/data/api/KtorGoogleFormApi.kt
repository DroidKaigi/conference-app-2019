package io.github.droidkaigi.confsched2019.data.api

import io.ktor.client.HttpClient
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.url
import io.ktor.http.parametersOf
import javax.inject.Inject

class KtorGoogleFormApi @Inject constructor(
    val httpClient: HttpClient
) : GoogleFormApi {

    companion object {
        const val FORM_URL = "https://docs.google.com/forms/" +
            "d/e/1FAIpQLScM2l7A2fRI1bidNKsswkHO6CIJ_bqwiWmncjyTRLP9JyPnUA/formResponse"
        const val KEY_SESSION_ID = "entry.587733619"
        const val KEY_SESSION_TITLE = "entry.893997240"
        const val KEY_TOTAL_EVALUATION = "entry.2026763339"
        const val KEY_RELEVANCY = "entry.499312343"
        const val KEY_AS_EXPECTED = "entry.715223183"
        const val KEY_DIFFICULTY = "entry.851615750"
        const val KEY_KNOWLEDGEABLE = "entry.1371898664"
        const val KEY_COMMENT = "entry.1819923854"
    }

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
                KEY_SESSION_ID to listOf(sessionId),
                KEY_SESSION_TITLE to listOf(sessionTitle),
                KEY_TOTAL_EVALUATION to listOf(totalEvaluation.toString()),
                KEY_RELEVANCY to listOf(relevancy.toString()),
                KEY_AS_EXPECTED to listOf(asExpected.toString()),
                KEY_DIFFICULTY to listOf(difficulty.toString()),
                KEY_KNOWLEDGEABLE to listOf(knowledgeable.toString()),
                KEY_COMMENT to listOf(comment)
            )
        ) {
            url(FORM_URL)
        }
    }
}
