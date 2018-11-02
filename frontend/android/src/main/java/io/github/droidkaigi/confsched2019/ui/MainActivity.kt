package io.github.droidkaigi.confsched2019.ui

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import io.github.droidkaigi.confsched2019.R
import io.github.droidkaigi.confsched2019.announcement.ui.AnnouncementFragment
import io.github.droidkaigi.confsched2019.announcement.ui.AnnouncementFragmentModule
import io.github.droidkaigi.confsched2019.databinding.ActivityMainBinding
import io.github.droidkaigi.confsched2019.session.ui.AllSessionsFragment
import io.github.droidkaigi.confsched2019.session.ui.AllSessionsFragmentModule
import io.github.droidkaigi.confsched2019.session.ui.SessionDetailFragment
import io.github.droidkaigi.confsched2019.session.ui.SessionDetailFragmentModule
import io.github.droidkaigi.confsched2019.user.actioncreator.UserActionCreator
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {
    @Inject lateinit var userActionCreator: UserActionCreator

    val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(
            this,
            R.layout.activity_main
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        val navController = findNavController(R.id.root_nav_host_fragment)
        setupActionBarWithNavController(navController, binding.drawerLayout)
        binding.navView.setupWithNavController(navController)
        binding.toolbar.setupWithNavController(navController, binding.drawerLayout)
    }

    override fun onStart() {
        super.onStart()
        userActionCreator.setupUser()
    }
}

@Module
abstract class MainActivityModule {
    @Binds abstract fun providesActivity(mainActivity: MainActivity): FragmentActivity

    @ContributesAndroidInjector(modules = [AllSessionsFragmentModule::class])
    abstract fun contributeAllSessionsFragment(): AllSessionsFragment

    @ContributesAndroidInjector(modules = [SessionDetailFragmentModule::class])
    abstract fun contributeSessionDetailFragment(): SessionDetailFragment

    @ContributesAndroidInjector(modules = [AnnouncementFragmentModule::class])
    abstract fun contributeAnnouncementFragment(): AnnouncementFragment

    @Module
    abstract class MainActivityBuilder {
        @ContributesAndroidInjector(modules = [MainActivityModule::class])
        abstract fun contributeMainActivity(): MainActivity
    }
}
