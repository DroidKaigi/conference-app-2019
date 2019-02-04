package io.github.droidkaigi.confsched2019

import android.app.ActivityManager
import android.content.Context
import com.facebook.stetho.Stetho
import com.squareup.leakcanary.LeakCanary
import io.github.droidkaigi.confsched2019.model.SystemProperty

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
        enableLogcatLogging()
    }

    override fun subscribeAnnouncementTopic(systemProperty: SystemProperty) {
        if (LeakCanary.isInAnalyzerProcess(this) || isHyperionCrashProcess()) {
            return
        }
        super.subscribeAnnouncementTopic(systemProperty)
    }

    private fun isHyperionCrashProcess(): Boolean {
        val pid = android.os.Process.myPid()
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val infos = manager.runningAppProcesses
        if (infos != null) {
            for (processInfo in infos) {
                if (processInfo.pid == pid) {
                    return processInfo.processName?.endsWith(":crash") ?: false
                }
            }
        }
        return false
    }
}
