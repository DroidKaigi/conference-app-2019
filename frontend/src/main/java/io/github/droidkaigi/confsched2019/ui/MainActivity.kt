package io.github.droidkaigi.confsched2019.ui

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import io.github.droidkaigi.confsched2019.R
import io.github.droidkaigi.confsched2019.session.ui.SessionDetailFragment
import io.github.droidkaigi.confsched2019.session.ui.SessionDetailFragmentModule
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
        setContentView(R.layout.activity_main)
    }
}

@Module
interface MainActivityModule {
    @Binds
    fun providesActivity(mainActivity: MainActivity): FragmentActivity

    @ContributesAndroidInjector(modules = [MainFragmentModule::class])
    fun contributeMainFragment(): MainFragment

    @ContributesAndroidInjector(modules = [SessionDetailFragmentModule::class])
    fun contributeSessionDetailFragment(): SessionDetailFragment

    @Module
    abstract class MainActivityBuilder {
        @ContributesAndroidInjector(modules = [MainActivityModule::class])
        abstract fun contributeMainActivity(): MainActivity
    }
}
