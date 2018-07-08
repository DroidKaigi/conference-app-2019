package io.github.droidkaigi.confsched2019.session.data.api.response

import kotlinx.serialization.Serializable

@Serializable
data class Response(
        val sessions: List<Session>

//        val rooms: List<Room>?,
//        val speakers: List<Speaker>?,
//        val categories: List<Category>?
)

