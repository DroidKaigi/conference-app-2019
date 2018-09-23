package io.github.droidkaigi.confsched2019.data.api

import io.github.droidkaigi.confsched2019.data.api.response.Response
import io.github.droidkaigi.confsched2019.data.api.response.ResponseImpl
import kotlinx.coroutines.experimental.Deferred
import retrofit2.Retrofit
import retrofit2.http.GET
import javax.inject.Inject

class RetrofitSessionApi @Inject constructor(
    retrofit: Retrofit
) : SessionApi {
    val service = retrofit.create(SessionsApi::class.java)

    interface SessionsApi {
        @GET("all")
        fun getSessions(): Deferred<ResponseImpl>
    }

    override suspend fun getSessions(): Response {
        return service
            .getSessions()
            .await()
    }
}
