package io.github.droidkaigi.confsched2019.session.model

sealed class Action{
    data class SessionsLoaded(val sessions: List<Session>): Action()
}