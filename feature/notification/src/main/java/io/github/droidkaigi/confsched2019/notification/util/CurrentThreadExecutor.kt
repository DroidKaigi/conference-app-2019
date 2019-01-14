package io.github.droidkaigi.confsched2019.notification.util

import java.util.concurrent.Executor

val currentThreadExecutor = Executor { command -> command.run() }
