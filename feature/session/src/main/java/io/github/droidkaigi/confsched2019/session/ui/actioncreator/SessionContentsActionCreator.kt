package io.github.droidkaigi.confsched2019.session.ui.actioncreator

import androidx.lifecycle.Lifecycle
import arrow.Kind
import arrow.core.Either
import arrow.core.extensions.either.applicativeError.handleErrorWith
import arrow.core.getOrElse
import arrow.effects.ForIO
import arrow.effects.extensions.io.fx.fx
import arrow.effects.extensions.io.unsafeRun.runNonBlocking
import arrow.unsafe
import io.github.droidkaigi.confsched2019.action.Action
import io.github.droidkaigi.confsched2019.data.repository.SessionRepository
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.dispatcher.Dispatcher
import io.github.droidkaigi.confsched2019.ext.android.coroutineScope
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.Message
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.system.actioncreator.ErrorHandler
import io.github.droidkaigi.confsched2019.util.SessionAlarm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import timber.log.debug
import javax.inject.Inject

@PageScope
class SessionContentsActionCreator @Inject constructor(
    override val dispatcher: Dispatcher,
    private val sessionRepository: SessionRepository,
    @PageScope private val lifecycle: Lifecycle,
    private val sessionAlarm: SessionAlarm
) : CoroutineScope by lifecycle.coroutineScope,
    ErrorHandler {
    fun refresh() = launch {
        try {
            Timber.debug { "SessionContentsActionCreator: refresh start" }
            dispatcher.dispatchLoadingState(LoadingState.LOADING)
            Timber.debug { "SessionContentsActionCreator: At first, load db data" }
            // At first, load db data
            val sessionContents = sessionRepository.sessionContents()
            dispatcher.dispatch(Action.SessionContentsLoaded(sessionContents))

            // fetch api data
            Timber.debug { "SessionContentsActionCreator: fetch api data" }
            sessionRepository.refresh()

            // reload db data
            Timber.debug { "SessionContentsActionCreator: reload db data" }
            val refreshedSessionContents = sessionRepository.sessionContents()
            dispatcher.dispatch(Action.SessionContentsLoaded(refreshedSessionContents))
            Timber.debug { "SessionContentsActionCreator: refresh end" }
            dispatcher.dispatchLoadingState(LoadingState.LOADED)
        } catch (e: Exception) {
            onError(e)
            dispatcher.dispatchLoadingState(LoadingState.INITIALIZED)
        }
    }

    fun load() {
        launch {
            try {
                dispatcher.dispatchLoadingState(LoadingState.LOADING)
                val sessionContents = sessionRepository.sessionContents()
                dispatcher.dispatch(Action.SessionContentsLoaded(sessionContents))
                dispatcher.dispatchLoadingState(LoadingState.LOADED)
            } catch (e: Exception) {
                onError(e)
                dispatcher.dispatchLoadingState(LoadingState.INITIALIZED)
            }
        }
    }

    fun toggleFavorite(session: Session) {
        launch {
            val nextLoadingState = async(
                fx {
                    !effect {
                        dispatcher.dispatchLoadingState(LoadingState.LOADING)
                        sessionRepository.toggleFavorite(session)
                        sessionAlarm.toggleRegister(session)
                        val sessionContents = sessionRepository.sessionContents()
                        dispatcher.dispatch(Action.SessionContentsLoaded(sessionContents))
                        LoadingState.LOADED
                    }
                }
            ).await()
                .handleErrorWith {
                    dispatcher.launchAndDispatch(
                        Action.ShowProcessingMessage(
                            Message.of(
                                R.string.session_favorite_connection_error
                            )
                        )
                    )

                    Either.right(LoadingState.INITIALIZED)
                }
                .getOrElse {
                    LoadingState.INITIALIZED
                }

            dispatcher.dispatchLoadingState(nextLoadingState)
        }
    }

    @UseExperimental(InternalCoroutinesApi::class)
    private suspend fun <A> async(io: Kind<ForIO, A>): Deferred<Either<Throwable, A>> =
        suspendCancellableCoroutine { continuation ->
            unsafe {
                runNonBlocking({
                    io
                }, { either ->
                    continuation.tryResume(async { either })
                })
            }
        }

    private suspend fun Dispatcher.dispatchLoadingState(loadingState: LoadingState) {
        println("Fragment: Dispatch $loadingState")
        dispatch(Action.SessionLoadingStateChanged(loadingState))
    }

    private fun Dispatcher.launchAndDispatchLoadingState(loadingState: LoadingState) {
        println("Fragment: Dispatch $loadingState")
        launchAndDispatch(Action.SessionLoadingStateChanged(loadingState))
    }
}
