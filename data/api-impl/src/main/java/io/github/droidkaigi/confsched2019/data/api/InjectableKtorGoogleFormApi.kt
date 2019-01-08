package io.github.droidkaigi.confsched2019.data.api

import io.ktor.client.HttpClient
import javax.inject.Inject

class InjectableKtorGoogleFormApi @Inject constructor(httpClient: HttpClient) : KtorGoogleFormApi(httpClient)
