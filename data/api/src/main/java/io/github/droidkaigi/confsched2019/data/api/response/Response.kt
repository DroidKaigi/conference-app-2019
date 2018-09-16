package io.github.droidkaigi.confsched2019.data.api.response

interface Response {
    val sessions: List<SessionResponse>
    val rooms: List<RoomResponse>?
    val speakers: List<SpeakerResponse>?
    val categories: List<CategoryResponse>?
}
