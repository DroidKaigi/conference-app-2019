package io.github.droidkaigi.confsched2019.session.data.api

import io.github.droidkaigi.confsched2019.session.data.api.response.Response
import io.github.droidkaigi.confsched2019.session.model.Session
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
        fun getSessions(): Deferred<Response>
    }

    override suspend fun getSessions(): List<Session.SpeechSession> {
        return service
                .getSessions()
                .await()
                .let {
                    it.sessions.map { apiSession ->
                        Session.SpeechSession(
                                id = apiSession.id,
                                desc = apiSession.description,
                                startTime = apiSession.startsAt,
                                endTime = apiSession.endsAt,
                                title = apiSession.title
                        )
                    }
                }
    }
}