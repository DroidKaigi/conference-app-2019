package io.github.droidkaigi.confsched2019.data.api

import io.github.droidkaigi.confsched2019.data.api.response.SponsorResponse
import io.github.droidkaigi.confsched2019.data.api.response.SponsorResponseImpl
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.http.GET
import javax.inject.Inject
import javax.inject.Named

class RetrofitSponsorApi @Inject constructor(
    @Named("SPONSOR") retrofit: Retrofit
) : SponsorApi {
    val service: SponsorsApi = retrofit.create(SponsorsApi::class.java)

    interface SponsorsApi {
        @GET("sponsors.json")
        fun getSponsors(): Deferred<SponsorResponseImpl>
    }

    override suspend fun getSponsors(): SponsorResponse = service
        .getSponsors()
        .await()
}
