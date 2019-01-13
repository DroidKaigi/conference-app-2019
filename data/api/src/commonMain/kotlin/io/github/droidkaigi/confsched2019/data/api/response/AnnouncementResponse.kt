package io.github.droidkaigi.confsched2019.data.api.response

import com.soywiz.klock.DateTime

interface AnnouncementResponse {
    val id: Long
    val title: String
    val content: String
    val type: String
    val date: DateTime
}
