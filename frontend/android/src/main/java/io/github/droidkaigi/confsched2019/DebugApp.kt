package io.github.droidkaigi.confsched2019

import android.util.Log
import com.facebook.stetho.Stetho
import com.squareup.leakcanary.LeakCanary
import io.github.droidkaigi.confsched2019.util.LogLevel
import io.github.droidkaigi.confsched2019.util.logHandler

class DebugApp : App() {
    override fun onCreate() {
        super.onCreate()
        setupLeakCanary()
        setupStetho()
        setupLogHandler()
    }

    private fun setupLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
    }

    private fun setupStetho() {
        Stetho.initializeWithDefaults(this)
    }

    private fun setupLogHandler() {
        logHandler = { logLevel: LogLevel, tag: String, e: Throwable?, logHandler: () -> String ->
            val message = if (e != null) {
                logHandler() + Log.getStackTraceString(e)
            } else {
                logHandler()
            }
            Log.println(
                when (logLevel) {
                    LogLevel.DEBUG -> Log.DEBUG
                    LogLevel.WARN -> Log.WARN
                    LogLevel.ERROR -> Log.ERROR
                },
                tag,
                message
            )
        }
    }
}
