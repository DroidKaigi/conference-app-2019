package io.github.droidkaigi.confsched2019.session.data.response

import kotlinx.serialization.Optional
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

@Serializable
data class Response(
        val sessions: List<Session>

//        val rooms: List<Room>?,
//        val speakers: List<Speaker>?,
//        val categories: List<Category>?
)

