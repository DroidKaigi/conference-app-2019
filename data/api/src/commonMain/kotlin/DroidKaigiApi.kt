package io.github.droidkaigi.confsched2019.data.api

import io.github.droidkaigi.confsched2019.data.api.response.Response

interface DroidKaigiApi {
    suspend fun getSessions(): Response
}
