package io.github.droidkaigi.confsched2019.data.api.response

interface SpeakerResponse {
    val firstName: String?
    val lastName: String?
    val profilePicture: String?
    val sessions: List<Int?>?
    val tagLine: String?
    val isTopSpeaker: Boolean?
    val bio: String?
    val fullName: String?
    val links: List<LinkResponse?>?
    val id: String?
}
