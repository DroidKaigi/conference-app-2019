package io.github.droidkaigi.confsched2019.data.api.response

import kotlinx.serialization.Serializable

@Serializable
data class SpeakerResponseImpl(
    override val firstName: String?,
    override val lastName: String?,
    override val profilePicture: String?,
    override val sessions: List<Int?>?,
    override val tagLine: String?,
    override val isTopSpeaker: Boolean?,
    override val bio: String?,
    override val fullName: String?,
    override val links: List<LinkResponseImpl?>?,
    override val id: String?
) : SpeakerResponse
