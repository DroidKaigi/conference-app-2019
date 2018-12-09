package io.github.droidkaigi.confsched2019.data.api

import dagger.Binds
import dagger.Module
import dagger.Provides
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.serialization.json.JSON

@Module(includes = [ApiModule.Providers::class])
internal abstract class ApiModule {
    @Binds abstract fun DroidKaigiApi(impl: KtorDroidKaigiApi): DroidKaigiApi

    @Module
    internal object Providers {
        @JvmStatic @Provides fun httpClient(): HttpClient {
            return HttpClient {
                install(JsonFeature) {
                    serializer = KotlinxSerializer(JSON.nonstrict)
                }
            }
        }
    }
}
