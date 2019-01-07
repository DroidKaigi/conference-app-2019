package io.github.droidkaigi.confsched2019.data.api

import dagger.Binds
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import javax.inject.Named
import kotlinx.serialization.json.JSON
import okhttp3.logging.HttpLoggingInterceptor

@Module(includes = [ApiModule.Providers::class])
internal abstract class ApiModule {
    @Binds abstract fun DroidKaigiApi(impl: KtorDroidKaigiApi): DroidKaigiApi
    @Binds abstract fun GoogleFormApi(impl: KtorGoogleFormApi): GoogleFormApi

    @Module
    internal object Providers {
        @JvmStatic @Provides fun httpClient(): HttpClient {
            return HttpClient(OkHttp) {
                engine {
                    if (BuildConfig.DEBUG) {
                        val loggingInterceptor = HttpLoggingInterceptor()
                        loggingInterceptor.level = HttpLoggingInterceptor.Level.HEADERS
                        addInterceptor(loggingInterceptor)
                    }
                }
                install(JsonFeature) {
                    serializer = KotlinxSerializer(JSON.nonstrict)
                }
            }
        }

        @JvmStatic @Provides @Named("apiEndpoint") fun apiEndpoint(): String {
            return BuildConfig.API_ENDPOINT
        }
    }
}
