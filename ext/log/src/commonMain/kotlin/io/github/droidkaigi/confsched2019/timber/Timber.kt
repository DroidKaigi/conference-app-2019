@file:Suppress("NOTHING_TO_INLINE")

package io.github.droidkaigi.confsched2019.timber

import timber.log.Timber

inline fun Timber.assert(throwable: Throwable) {
    Timber.log(ASSERT, null, throwable, null)
}

inline fun Timber.error(throwable: Throwable) {
    Timber.log(ERROR, null, throwable, null)
}

inline fun Timber.warn(throwable: Throwable) {
    Timber.log(WARNING, null, throwable, null)
}

inline fun Timber.info(throwable: Throwable) {
    Timber.log(INFO, null, throwable, null)
}

inline fun Timber.debug(throwable: Throwable) {
    Timber.log(DEBUG, null, throwable, null)
}

inline fun Timber.verbose(throwable: Throwable) {
    Timber.log(VERBOSE, null, throwable, null)
}
