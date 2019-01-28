package io.github.droidkaigi.confsched2019.system.actioncreator

import com.google.firebase.FirebaseApiNotAvailableException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestoreException
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.model.Message
import io.github.droidkaigi.confsched2019.system.BuildConfig
import io.github.droidkaigi.confsched2019.system.R
import io.github.droidkaigi.confsched2019.timber.error
import io.ktor.client.features.BadResponseStatusException
import kotlinx.coroutines.CancellationException
import timber.log.Timber
import timber.log.debug
import timber.log.error
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

interface ErrorHandler {
    val dispatcher: Dispatcher
    fun onError(e: Throwable, msg: String? = null) {
        if (e.cause is CancellationException) {
            Timber.debug(e) {
                "coroutine canceled"
            }
            return
        }
        when (e) {
            is CancellationException ->
                Timber.debug(e) {
                    "coroutine canceled"
                }
            is UnknownHostException,
            is SocketTimeoutException,
            is ConnectException,
            is FirebaseFirestoreException,
            is FirebaseApiNotAvailableException,
            is FirebaseNetworkException -> {
                val message = Message.of(R.string.system_error_network)
                Timber.error(e)
                dispatcher.launchAndDispatch(Action.ShowProcessingMessage(message))
            }
            is BadResponseStatusException -> {
                val message = Message.of(R.string.system_error_server)
                Timber.error(e)
                dispatcher.launchAndDispatch(Action.ShowProcessingMessage(message))
            }
            else -> {
                val message = Message.of(msg ?: e.message ?: e.javaClass.name ?: "")
                Timber.error(e) {
                    (message as Message.TextMessage).message
                }
                if (BuildConfig.DEBUG) throw UnknownException(e)
                dispatcher.launchAndDispatch(Action.ShowProcessingMessage(message))
            }
        }
    }
}

class UnknownException(e: Throwable) : RuntimeException(e)
