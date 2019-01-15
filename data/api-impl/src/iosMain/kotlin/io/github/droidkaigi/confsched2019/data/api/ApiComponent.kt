package io.github.droidkaigi.confsched2019.data.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.ios.Ios
import io.ktor.client.features.UserAgent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.serialization.json.JSON
import platform.Foundation.NSBundle
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer

// TODO: Replace with DI tools.
internal fun generateHttpClient(): HttpClient {
    val version = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as String
    return HttpClient(Ios) {
        install(UserAgent) {
            agent = "official-app-2019/$version"
        }
        install(JsonFeature) {
            serializer = KotlinxSerializer(JSON.nonstrict)
        }
    }
}

fun generateDroidKaigiApi(): DroidKaigiApi {
    return KtorDroidKaigiApi(generateHttpClient(), apiEndpoint(), MainLoopDispatcher + CoroutineExceptionHandler { coroutineContext, throwable ->
        throwable.printStackTrace()
    })
}

fun generateGoogleFormApi(): GoogleFormApi {
    return KtorGoogleFormApi(generateHttpClient())
}
