package io.github.droidkaigi.confsched2019.session.data

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import com.jakewharton.retrofit2.converter.kotlinx.serialization.stringBased
import io.github.droidkaigi.confsched2019.session.data.response.InstantSerializer
//import io.github.droidkaigi.confsched2019.session.actioncreator.response.InstantSerializer
import io.github.droidkaigi.confsched2019.session.data.response.Response
import io.github.droidkaigi.confsched2019.session.data.response.Session
import kotlinx.coroutines.experimental.Deferred
import kotlinx.serialization.*
import kotlinx.serialization.internal.PrimitiveDesc
import kotlinx.serialization.json.JSON
import okhttp3.MediaType
import okhttp3.OkHttpClient
import org.threeten.bp.Instant
import retrofit2.Retrofit
import retrofit2.http.GET


val contentType = MediaType.parse("application/json")!!
val json = JSON(
        nonstrict = true,
        context = SerialContext().apply { registerSerializer(Instant::class, InstantSerializer) }
)
val retrofit = Retrofit.Builder()
        .baseUrl("https://sessionize.com/api/v2/xtj7shk8/view/")
        .callFactory(OkHttpClient.Builder()
                .build())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addConverterFactory(stringBased(contentType,
                json::parse,
                json::stringify))
        .build()

interface SessionsApi {
    @GET("all")
    fun getSessions(): Deferred<Response>
}

val service = retrofit.create(SessionsApi::class.java)
suspend fun getSessions(): Response {
    return service.getSessions().await()
}