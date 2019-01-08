package io.github.droidkaigi.confsched2019.data.api

import io.github.droidkaigi.confsched2019.api.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

internal class UserAgentInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request().newBuilder().apply {
            addHeader("User-Agent", "official-app-2019/${BuildConfig.VERSION_CODE}")
        }.build())
    }
}
