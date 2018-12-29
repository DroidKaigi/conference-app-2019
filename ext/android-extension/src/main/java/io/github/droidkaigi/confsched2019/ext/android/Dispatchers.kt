package io.github.droidkaigi.confsched2019.ext.android

object
Dispatchers {
    val Main get() = CoroutinePlugin.mainDispatcher
    val IO get() = CoroutinePlugin.ioDispatcher
    val Default get() = CoroutinePlugin.defaultDispatcher
}
