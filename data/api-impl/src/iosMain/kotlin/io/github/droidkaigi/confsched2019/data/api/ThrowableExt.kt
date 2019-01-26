package io.github.droidkaigi.confsched2019.data.api

import io.ktor.client.engine.ios.IosHttpRequestException
import io.ktor.util.KtorExperimentalAPI
import platform.Foundation.NSError

/**
 * Obtain origin NSError if supported.
 *
 * - NOTE:
 * When occurred a client/network error, Ktor iOS HttpClient throws [IosHttpRequestException].
 * [IosHttpRequestException] has a useful [NSError] object.
 * However [IosHttpRequestException] is not visible at commonModule and iOS Swift module.
 * So we provide workaround for iOS Swift module obtain [NSError] from [Throwable].
 */
@KtorExperimentalAPI
val Throwable.originNSError: NSError?
    get() = (this as? IosHttpRequestException)?.origin
