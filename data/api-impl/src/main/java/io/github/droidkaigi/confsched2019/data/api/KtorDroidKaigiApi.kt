package io.github.droidkaigi.confsched2019.data.api

import io.github.droidkaigi.confsched2019.data.api.response.Response
import io.github.droidkaigi.confsched2019.data.api.response.ResponseImpl
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.url
import javax.inject.Inject

class KtorDroidKaigiApi @Inject constructor(
    val httpClient: HttpClient
) : DroidKaigiApi {

    override suspend fun getSessions(): Response {
        return httpClient.get<ResponseImpl> {
            url("https://sessionize.com/api/v2/xc8zp3xu/view/all")
        }
    }
}
