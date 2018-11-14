package io.github.droidkaigi.confsched2019.util

enum class LogLevel {
    DEBUG, WARN, ERROR
}

var logHandler = { logLevel: LogLevel, tag: String, e: Throwable?, messageHandler: () -> String -> }

fun logd(
    tag: String = "droidkaigi",
    e: Throwable? = null,
    messageHandler: () -> String = { "" }
) = logHandler(LogLevel.DEBUG, tag, e, messageHandler)

fun logw(
    tag: String = "droidkaigi",
    e: Throwable? = null,
    messageHandler: () -> String = { "" }
) = logHandler(LogLevel.WARN, tag, e, messageHandler)

fun loge(
    tag: String = "droidkaigi",
    e: Throwable? = null,
    messageHandler: () -> String = { "" }
) = logHandler(LogLevel.ERROR, tag, e, messageHandler)
