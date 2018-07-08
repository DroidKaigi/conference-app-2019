package io.github.droidkaigi.confsched2019

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import io.github.droidkaigi.confsched2019.session.actioncreator.SessionActionCreator
import kotlinx.coroutines.experimental.launch

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)

        launch {
            SessionActionCreator().load()
        }
    }
}
