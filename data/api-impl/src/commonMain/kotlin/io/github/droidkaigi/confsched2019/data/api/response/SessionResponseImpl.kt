package io.github.droidkaigi.confsched2019.data.api.response

import kotlinx.serialization.Optional
import kotlinx.serialization.Serializable

@Serializable
data class SessionResponseImpl(
    override val id: String,
    override val isServiceSession: Boolean,
    override val title: String,
    @Optional override val englishTitle: String? = null,
    override val speakers: List<String>,
    override val description: String,
    override val startsAtWithTZ: String,
    override val endsAtWithTZ: String,
    override val roomId: Int,
    override val categoryItems: List<Int>,
    override val questionAnswers: List<QuestionAnswerResponseImpl>,
    override val sessionType: String?,
    @Optional override val message: SessionMessageResponseImpl? = null,
    override val isPlenumSession: Boolean,
    override val forBeginners: Boolean,
    override val interpretationTarget: Boolean,
    override val videoUrl: String?,
    override val slideUrl: String?
) : SessionResponse
