package io.github.droidkaigi.confsched2019.model

sealed class Message {
    class ResourceIdMessage(val messageId: Int) : Message()
    class TextMessage(val message: String) : Message()

    companion object {
        fun of(messageId: Int): Message =
            ResourceIdMessage(messageId)

        fun of(message: String): Message =
            TextMessage(message)
    }
}
