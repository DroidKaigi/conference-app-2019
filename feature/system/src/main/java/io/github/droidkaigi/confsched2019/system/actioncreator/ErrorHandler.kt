package io.github.droidkaigi.confsched2019.system.actioncreator

import com.google.firebase.FirebaseApiNotAvailableException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestoreException
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.model.ErrorMessage
import io.github.droidkaigi.confsched2019.system.BuildConfig
import io.github.droidkaigi.confsched2019.system.R
import io.github.droidkaigi.confsched2019.util.logd
import io.github.droidkaigi.confsched2019.util.loge
import io.ktor.client.features.BadResponseStatusException
import kotlinx.coroutines.CancellationException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

interface ErrorHandler {
    val dispatcher: Dispatcher
    fun onError(e: Throwable, msg: String? = null) {
        if (e.cause is CancellationException) {
            logd(e = e) {
                "coroutine canceled"
            }
            return
        }
        when (e) {
            is CancellationException ->
                logd(e = e) {
                    "coroutine canceled"
                }
            is UnknownHostException,
            is SocketTimeoutException,
            is ConnectException,
            is FirebaseFirestoreException,
            is FirebaseApiNotAvailableException,
            is FirebaseNetworkException -> {
                val message = ErrorMessage.of(R.string.system_error_network, e)
                loge(e = e)
                dispatcher.launchAndDispatch(Action.Error(message))
            }
            is BadResponseStatusException -> {
                val message = ErrorMessage.of(R.string.system_error_server, e)
                loge(e = e)
                dispatcher.launchAndDispatch(Action.Error(message))
            }
            else -> {
                val message = ErrorMessage.of(msg ?: e.message ?: e.javaClass.name ?: "", e)
                loge(e = e) {
                    (message as ErrorMessage.Message).message
                }
                if (BuildConfig.DEBUG) throw UnknownException(e)
                dispatcher.launchAndDispatch(Action.Error(message))
            }
        }
    }
}

class UnknownException(e: Throwable) : RuntimeException(e)
