package io.github.droidkaigi.confsched2019.data.api.response

import kotlinx.serialization.Serializable

@Serializable
data class RoomResponseImpl(
    override val name: String?,
    override val id: Int?,
    override val sort: Int?
) : RoomResponse
