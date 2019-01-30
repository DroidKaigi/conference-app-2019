package io.github.droidkaigi.confsched2019.data.api

import io.github.droidkaigi.confsched2019.data.api.parameter.LangParameter
import io.github.droidkaigi.confsched2019.data.api.response.*
import io.ktor.client.HttpClient
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.ContentType
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JSON
import kotlinx.serialization.list
import kotlin.coroutines.CoroutineContext

open class KtorDroidKaigiApi constructor(
    val httpClient: HttpClient,
    val apiEndpoint: String,
    val coroutineDispatcherForCallback: CoroutineContext?
) : DroidKaigiApi {
    override suspend fun getSessions(): Response {
        // We are separate getting response string and parsing for Kotlin Native
        val rawResponse = httpClient.get<String> {
            url("$apiEndpoint/timetable")
            accept(ContentType.Application.Json)
        }
        return JSON.nonstrict.parse(ResponseImpl.serializer(), rawResponse)
    }

    override fun getSessions(
        callback: (response: Response) -> Unit,
        onError: (error: Exception) -> Unit
    ) {
        GlobalScope.launch(requireNotNull(coroutineDispatcherForCallback)) {
            try {
                val response = getSessions()
                callback(response)
            } catch (ex: Exception) {
                onError(ex)
            }
        }
    }

    override fun getSessionsAsync(): Deferred<Response> =
        GlobalScope.async(requireNotNull(coroutineDispatcherForCallback)) {
            getSessions()
        }

    override suspend fun getAnnouncements(lang: LangParameter): AnnouncementListResponse {
        val rawResponse = httpClient.get<String> {
            url("$apiEndpoint/announcements?language=${lang.name}")
            accept(ContentType.Application.Json)
        }

        return JSON.parse(AnnouncementResponseImpl.serializer().list, rawResponse)
    }

    override suspend fun getSponsors(): SponsorResponse {
        val rawResponse = httpClient.get<String> {
            url("$apiEndpoint/sponsors")
            accept(ContentType.Application.Json)
        }

        return JSON.nonstrict.parse(SponsorResponseImpl.serializer(), rawResponse)
    }

    override suspend fun getStaffs(): StaffResponse {
        val rawResponse = httpClient.get<String> {
            url("$apiEndpoint/staffs")
            accept(ContentType.Application.Json)
        }

        return StaffResponseImpl(JSON.parse(StaffItemResponseImpl.serializer().list, rawResponse))
    }

    override suspend fun getContributorList(): ContributorResponse {
        val rawResponse = httpClient.get<String> {
            url("$apiEndpoint/contributors")
            accept(ContentType.Application.Json)
        }

        return ContributorResponseImpl(JSON.parse(ContributorItemReesponseImpl.serializer().list, rawResponse))
    }
}
