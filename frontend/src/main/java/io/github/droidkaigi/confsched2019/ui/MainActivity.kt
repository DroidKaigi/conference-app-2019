package io.github.droidkaigi.confsched2019.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.github.droidkaigi.confsched2019.R
import io.github.droidkaigi.confsched2019.session.ui.AllSessionsFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, AllSessionsFragment.newInstance())
                    .commitNow()
        }
    }
}

@Module
abstract class MainActivityModule {
    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity
}