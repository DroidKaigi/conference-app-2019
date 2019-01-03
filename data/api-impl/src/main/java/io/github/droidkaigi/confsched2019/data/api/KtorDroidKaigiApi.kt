package io.github.droidkaigi.confsched2019.data.api

import io.github.droidkaigi.confsched2019.data.api.response.Response
import io.github.droidkaigi.confsched2019.data.api.response.ResponseImpl
import io.github.droidkaigi.confsched2019.data.api.response.SponsorResponse
import io.github.droidkaigi.confsched2019.data.api.response.SponsorResponseImpl
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.url
import javax.inject.Inject

class KtorDroidKaigiApi @Inject constructor(
    val httpClient: HttpClient
) : DroidKaigiApi {
    override suspend fun getSponsors(): SponsorResponse {
        return httpClient.get<SponsorResponseImpl> {
            url("https://droidkaigi.jp/2019/sponsors.json")
        }
    }

    override suspend fun getSessions(): Response {
        return httpClient.get<ResponseImpl> {
            url("https://sessionize.com/api/v2/xtj7shk8/view/all")
        }
    }
}
