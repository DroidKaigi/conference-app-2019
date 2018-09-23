package io.github.droidkaigi.confsched2019.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import io.github.droidkaigi.confsched2019.R
import io.github.droidkaigi.confsched2019.session.ui.AllSessionsFragment
import io.github.droidkaigi.confsched2019.session.ui.AllSessionsFragmentModule
import io.github.droidkaigi.confsched2019.user.actioncreator.UserActionCreator
import io.github.droidkaigi.confsched2019.user.store.UserStore
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var userActionCreator: UserActionCreator
    @Inject
    lateinit var userStore: UserStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, AllSessionsFragment.newInstance())
                .commitNow()
        }
    }

    override fun onStart() {
        super.onStart()
        userActionCreator.setupUser()
    }
}

@Module
interface MainActivityModule {
    @Binds
    fun providesAppCompatActivity(mainActivity: MainActivity): AppCompatActivity

    @ContributesAndroidInjector(modules = [AllSessionsFragmentModule::class])
    fun contributeAllSessionsFragment(): AllSessionsFragment

    @Module
    abstract class MainActivityBuilder {
        @ContributesAndroidInjector(modules = [MainActivityModule::class])
        abstract fun contributeMainActivity(): MainActivity
    }
}
