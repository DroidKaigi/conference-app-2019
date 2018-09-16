package io.github.droidkaigi.confsched2019.data.api.response

import kotlinx.serialization.Optional
import kotlinx.serialization.Serializable

@Serializable
data class SessionResponseImpl(
        override val id: String,
        override val isServiceSession: Boolean,
        override val isPlenumSession: Boolean,
        override val speakers: List<String>,
        override val description: String,
        override val startsAt: String,
        override val title: String,
        override val endsAt: String,
        override val roomId: Int,
        override val categoryItems: List<Int>,
        @Optional
        override val message: SessionMessageResponseImpl? = null
) : SessionResponse

