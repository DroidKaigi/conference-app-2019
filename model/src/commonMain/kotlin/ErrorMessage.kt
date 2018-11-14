package io.github.droidkaigi.confsched2019.model

sealed class ErrorMessage(val e: Throwable) {
    class Message(val message: String, e: Throwable) : ErrorMessage(e)
    class ResourceIdMessage(val messageId: Int, e: Throwable) : ErrorMessage(e)
    companion object {
        fun of(message: String, e: Throwable): ErrorMessage = Message(message, e)
        fun of(messageId: Int, e: Throwable): ErrorMessage = ResourceIdMessage(messageId, e)
    }
}
