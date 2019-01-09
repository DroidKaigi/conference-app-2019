package io.github.droidkaigi.confsched2019.util

import timber.log.Timber

fun Timber.timberd(throwable: Throwable) {
    Timber.log(DEBUG, null, throwable, null)
}
