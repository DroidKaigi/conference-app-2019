package io.github.droidkaigi.confsched2019.data.api

import io.ktor.client.HttpClient
import javax.inject.Inject
import javax.inject.Named

class InjectableKtorDroidKaigiApi @Inject constructor(
    httpClient: HttpClient,
    @Named("apiEndpoint") apiEndpoint: String
) : KtorDroidKaigiApi(httpClient, apiEndpoint, null)
