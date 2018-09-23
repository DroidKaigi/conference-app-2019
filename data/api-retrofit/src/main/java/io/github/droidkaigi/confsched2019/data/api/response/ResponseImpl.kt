package io.github.droidkaigi.confsched2019.data.api.response

import kotlinx.serialization.Serializable

@Serializable
data class ResponseImpl(
    override val sessions: List<SessionResponseImpl>,
    override val rooms: List<RoomResponseImpl>?,
    override val speakers: List<SpeakerResponseImpl>?,
    override val categories: List<CategoryResponseImpl>?
) : Response

