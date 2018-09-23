package io.github.droidkaigi.confsched2019.data.api.response

import kotlinx.serialization.Serializable

@Serializable
data class SessionMessageResponseImpl(
    override val ja: String?,
    override val en: String?
) : SessionMessageResponse
