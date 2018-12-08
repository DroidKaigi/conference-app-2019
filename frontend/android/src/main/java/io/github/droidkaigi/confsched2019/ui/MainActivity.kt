package io.github.droidkaigi.confsched2019.ui

import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import io.github.droidkaigi.confsched2019.R
import io.github.droidkaigi.confsched2019.announcement.ui.AnnouncementFragment
import io.github.droidkaigi.confsched2019.announcement.ui.AnnouncementFragmentModule
import io.github.droidkaigi.confsched2019.databinding.ActivityMainBinding
import io.github.droidkaigi.confsched2019.ext.android.changed
import io.github.droidkaigi.confsched2019.model.ErrorMessage
import io.github.droidkaigi.confsched2019.session.di.SessionAssistedInjectModule
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.session.di.SessionPagesScope
import io.github.droidkaigi.confsched2019.session.ui.SessionDetailFragment
import io.github.droidkaigi.confsched2019.session.ui.SessionDetailFragmentModule
import io.github.droidkaigi.confsched2019.session.ui.SessionPagesFragment
import io.github.droidkaigi.confsched2019.session.ui.SessionPagesFragmentModule
import io.github.droidkaigi.confsched2019.session.ui.SpeakerFragment
import io.github.droidkaigi.confsched2019.session.ui.SpeakerFragmentModule
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.SessionsActionCreator
import io.github.droidkaigi.confsched2019.sponsor.ui.SponsorFragment
import io.github.droidkaigi.confsched2019.sponsor.ui.SponsorFragmentModule
import io.github.droidkaigi.confsched2019.system.store.SystemStore
import io.github.droidkaigi.confsched2019.user.actioncreator.UserActionCreator
import io.github.droidkaigi.confsched2019.user.store.UserStore
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {
    @Inject lateinit var userActionCreator: UserActionCreator
    @Inject lateinit var userStore: UserStore
    @Inject lateinit var sessionsActionCreator: SessionsActionCreator
    @Inject lateinit var systemStore: SystemStore

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
        navController.addOnNavigatedListener { _, destination ->
            val isWhiteTheme = destination.id != R.id.main
            binding.isWhiteTheme = isWhiteTheme
            if (23 <= Build.VERSION.SDK_INT) {
                window.decorView.systemUiVisibility = if (isWhiteTheme) {
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    0
                }
            }
            val toolbarContentsColor = ContextCompat.getColor(
                this, if (isWhiteTheme) android.R.color.black else R.color.white
            )
            binding.toolbar.navigationIcon?.setColorFilter(
                toolbarContentsColor,
                PorterDuff.Mode.SRC_ATOP
            )
            binding.toolbar.setTitleTextColor(toolbarContentsColor)
        }

        userStore.logined.changed(this) { loggedin ->
            if (loggedin) {
                sessionsActionCreator.refresh()
            }
        }
        systemStore.errorMsg.changed(this) { message ->
            val messageStr = when (message) {
                is ErrorMessage.ResourceIdMessage -> getString(message.messageId)
                is ErrorMessage.Message -> message.message
            }
            Snackbar.make(binding.root, messageStr, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onStart() {
        super.onStart()
        userActionCreator.setupUserIfNeeded()
    }
}

@Module
abstract class MainActivityModule {
    @Binds abstract fun providesActivity(mainActivity: MainActivity): FragmentActivity

    @SessionPagesScope
    @ContributesAndroidInjector(
        modules = [SessionPagesFragmentModule::class, SessionAssistedInjectModule::class]
    )
    abstract fun contributeSessionPagesFragment(): SessionPagesFragment

    @PageScope
    @ContributesAndroidInjector(
        modules = [SessionDetailFragmentModule::class, SessionAssistedInjectModule::class]
    )
    abstract fun contributeSessionDetailFragment(): SessionDetailFragment

    @PageScope
    @ContributesAndroidInjector(
        modules = [SpeakerFragmentModule::class, SessionAssistedInjectModule::class]
    )
    abstract fun contributeSpeakerFragment(): SpeakerFragment

    @PageScope
    @ContributesAndroidInjector(modules = [AnnouncementFragmentModule::class])
    abstract fun contributeAnnouncementFragment(): AnnouncementFragment

    @PageScope
    @ContributesAndroidInjector(modules = [SponsorFragmentModule::class])
    abstract fun contributeSponsorFragment(): SponsorFragment

    @Module
    companion object {
        @JvmStatic @Provides fun provideNavController(mainActivity: MainActivity): NavController {
            return Navigation
                .findNavController(mainActivity, R.id.root_nav_host_fragment)
        }
    }

    @Module
    abstract class MainActivityBuilder {
        @ContributesAndroidInjector(modules = [MainActivityModule::class])
        abstract fun contributeMainActivity(): MainActivity
    }
}
