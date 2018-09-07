package io.github.droidkaigi.confsched2019

import com.facebook.stetho.Stetho
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import io.github.droidkaigi.confsched2019.di.createAppComponent
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.SessionActionCreator
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class App : DaggerApplication() {
    @Inject
    lateinit var sessionActionCreator: SessionActionCreator

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        Stetho.initializeWithDefaults(this);

        launch {
            sessionActionCreator.load()
        }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return createAppComponent()
    }
}
