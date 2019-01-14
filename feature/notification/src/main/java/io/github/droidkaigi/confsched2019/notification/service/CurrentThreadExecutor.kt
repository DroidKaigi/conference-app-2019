package io.github.droidkaigi.confsched2019.notification.service

import java.util.concurrent.Executor

val currentThreadExecutor = Executor { command -> command.run() }
