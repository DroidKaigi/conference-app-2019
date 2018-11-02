package io.github.droidkaigi.confsched2019.data.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.jakewharton.retrofit2.converter.kotlinx.serialization.stringBased
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.JSON
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

@Module(includes = [ApiModule.Providers::class])
internal abstract class ApiModule {
    @Binds abstract fun sessionApi(impl: RetrofitSessionApi): SessionApi

    @Module
    internal object Providers {
        @JvmStatic @Provides fun retrofit(): Retrofit {
            val contentType = MediaType.get("application/json; charset=utf-8")
            val json = JSON.nonstrict
            return Retrofit.Builder()
                .baseUrl("https://sessionize.com/api/v2/xtj7shk8/view/")
                .callFactory(OkHttpClient.Builder()
                    .build())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(stringBased(contentType, json::parse, json::stringify))
                .build()
        }
    }
}
