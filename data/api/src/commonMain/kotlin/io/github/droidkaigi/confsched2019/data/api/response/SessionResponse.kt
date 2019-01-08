package io.github.droidkaigi.confsched2019.data.api.response

interface SessionResponse {
    val id: String
    val isServiceSession: Boolean
    val title: String
    val englishTitle: String?
    val speakers: List<String>
    val description: String
    val startsAt: String
    val endsAt: String
    val roomId: Int
    val categoryItems: List<Int>
    val questionAnswers: List<QuestionAnswerResponse>
    val message: SessionMessageResponse?
    val isPlenumSession: Boolean
    val sessionType: String?
    val interpretationTarget: Boolean
}
