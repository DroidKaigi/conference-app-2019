package io.github.droidkaigi.confsched2019.data.api.response

data class AnnouncementResponseImpl(
    override val id: Long,
    override val title: String,
    override val content: String,
    override val type: String,
    override val date: String
) : AnnouncementResponse
