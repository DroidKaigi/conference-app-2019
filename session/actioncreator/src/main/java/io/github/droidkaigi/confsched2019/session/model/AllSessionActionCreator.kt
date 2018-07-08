package io.github.droidkaigi.confsched2019.session.model

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import kotlin.coroutines.experimental.coroutineContext

class AllSessionActionCreator {
    suspend fun listen() = launch(coroutineContext + CommonPool) {
        // TODO: listen db data
    }

}

