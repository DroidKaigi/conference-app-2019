package io.github.droidkaigi.confsched2019

import com.facebook.stetho.Stetho
import com.squareup.leakcanary.LeakCanary
import timber.log.LogcatTree
import timber.log.Timber

class DebugApp : App() {
    override fun onCreate() {
        super.onCreate()
        setupLeakCanary()
        setupStetho()
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

    override fun setupLogHandler() {
        enableLogCatLogging()
    }
}
