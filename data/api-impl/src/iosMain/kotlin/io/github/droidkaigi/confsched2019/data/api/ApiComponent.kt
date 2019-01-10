package io.github.droidkaigi.confsched2019.data.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.ios.Ios
import io.ktor.client.features.UserAgent
import platform.Foundation.NSBundle

// TODO: Replace with DI tools.
internal fun generateHttpClient(): HttpClient {
    val version = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as String
    return HttpClient(Ios) {
        install(UserAgent) {
            agent = "official-app-2019/$version"
        }
    }
}

fun generateDroidKaigiApi(): DroidKaigiApi {
    return KtorDroidKaigiApi(generateHttpClient(), apiEndpoint())
}

fun generateGoogleFormApi(): GoogleFormApi {
    return KtorGoogleFormApi(generateHttpClient())
}
