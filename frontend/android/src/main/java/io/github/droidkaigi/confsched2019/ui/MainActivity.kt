package io.github.droidkaigi.confsched2019.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import io.github.droidkaigi.confsched2019.R
import io.github.droidkaigi.confsched2019.about.ui.AboutFragment
import io.github.droidkaigi.confsched2019.about.ui.AboutFragmentModule
import io.github.droidkaigi.confsched2019.announcement.ui.AnnouncementFragment
import io.github.droidkaigi.confsched2019.announcement.ui.AnnouncementFragmentModule
import io.github.droidkaigi.confsched2019.contributor.ContributorFragment
import io.github.droidkaigi.confsched2019.contributor.ContributorFragmentModule
import io.github.droidkaigi.confsched2019.contributor.di.ContributorAssistedInjectModule
import io.github.droidkaigi.confsched2019.databinding.ActivityMainBinding
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.ext.changed
import io.github.droidkaigi.confsched2019.floormap.ui.FloorMapFragment
import io.github.droidkaigi.confsched2019.floormap.ui.FloorMapFragmentModule
import io.github.droidkaigi.confsched2019.model.Message
import io.github.droidkaigi.confsched2019.session.di.SessionAssistedInjectModule
import io.github.droidkaigi.confsched2019.session.ui.SearchFragment
import io.github.droidkaigi.confsched2019.session.ui.SearchFragmentModule
import io.github.droidkaigi.confsched2019.session.ui.SessionDetailFragment
import io.github.droidkaigi.confsched2019.session.ui.SessionDetailFragmentModule
import io.github.droidkaigi.confsched2019.session.ui.SessionPagesFragment
import io.github.droidkaigi.confsched2019.session.ui.SessionPagesFragmentModule
import io.github.droidkaigi.confsched2019.session.ui.SpeakerFragment
import io.github.droidkaigi.confsched2019.session.ui.SpeakerFragmentModule
import io.github.droidkaigi.confsched2019.session.ui.TabularFormSessionPagesFragment
import io.github.droidkaigi.confsched2019.session.ui.TabularFromSessionPagesFragmentModule
import io.github.droidkaigi.confsched2019.sponsor.ui.SponsorFragment
import io.github.droidkaigi.confsched2019.sponsor.ui.SponsorFragmentModule
import io.github.droidkaigi.confsched2019.staff.ui.StaffSearchFragment
import io.github.droidkaigi.confsched2019.staff.ui.StaffSearchFragmentModule
import io.github.droidkaigi.confsched2019.survey.ui.SessionSurveyFragment
import io.github.droidkaigi.confsched2019.survey.ui.SessionSurveyFragmentModule
import io.github.droidkaigi.confsched2019.system.actioncreator.ActivityActionCreator
import io.github.droidkaigi.confsched2019.system.store.SystemStore
import io.github.droidkaigi.confsched2019.ui.widget.StatusBarColorManager
import io.github.droidkaigi.confsched2019.user.actioncreator.UserActionCreator
import io.github.droidkaigi.confsched2019.user.store.UserStore
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(
            this,
            R.layout.activity_main
        )
    }

    @Inject lateinit var userActionCreator: UserActionCreator
    @Inject lateinit var systemStore: SystemStore
    @Inject lateinit var userStore: UserStore
    @Inject lateinit var activityActionCreator: ActivityActionCreator

    private val navController: NavController by lazy {
        findNavController(R.id.root_nav_host_fragment)
    }
    private val statusBarColors: StatusBarColorManager by lazy {
        StatusBarColorManager()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        setupNavigation()
        setupStatusBarColors()

        systemStore.message.changed(this) { message ->
            if (message == null) {
                return@changed
            }

            val messageStr: String = when (message) {
                is Message.ResourceIdMessage -> {
                    if (message.stringArgs.isEmpty()) {
                        getString(message.messageId)
                    } else {
                        getString(message.messageId, *message.stringArgs)
                    }
                }
                is Message.TextMessage -> message.message
            }
            Snackbar.make(binding.root, messageStr, Snackbar.LENGTH_LONG).show()
        }
        userStore.registered.changed(this) { registered ->
            if (!registered) {
                userActionCreator.load()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)

        navController.handleDeepLink(intent)
    }

    private fun setupNavigation() {
        val topLevelDestinationIds = setOf(R.id.main, R.id.about, R.id.announce, R.id.setting,
            R.id.floormap, R.id.sponsor, R.id.contributor)
        val appBarConfiguration = AppBarConfiguration(
            topLevelDestinationIds,
            binding.drawerLayout
        ) {
            onBackPressed()
            true
        }
        setupActionBarWithNavController(navController, appBarConfiguration)
        setupNavigationView()
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // check drawer menu item/ apply when back key pressed
            checkCurrentDestinationIdInDrawer(destination.id)

            // to avoid flickering, set current fragment background color to white
            // see https://github.com/DroidKaigi/conference-app-2019/pull/521
            supportFragmentManager.findFragmentById(R.id.root_nav_host_fragment)?.let { host ->
                val current = host.childFragmentManager.primaryNavigationFragment
                if (current !is SessionPagesFragment) {
                    current?.view?.setBackgroundColor(Color.WHITE)
                }
            }

            val config = PageConfiguration.getConfiguration(destination.id)
            binding.logo.isVisible = config.isShowLogoImage
            if (!config.hasTitle) supportActionBar?.title = ""

            if (config.hideToolbar) {
                supportActionBar?.hide()
            } else {
                supportActionBar?.show()
            }
            binding.isWhiteTheme = config.isWhiteTheme
            statusBarColors.isWhiteTheme = config.isWhiteTheme
            if (destination.id in topLevelDestinationIds) {
                binding.toolbar.setNavigationIcon(R.drawable.ic_hamburger)
            } else {
                binding.toolbar.setNavigationIcon(R.drawable.ic_back_arrow)
            }
            val toolbarContentsColor = ContextCompat.getColor(
                this, if (config.isWhiteTheme) android.R.color.black else R.color.white
            )
            binding.toolbar.navigationIcon?.setColorFilter(
                toolbarContentsColor,
                PorterDuff.Mode.SRC_ATOP
            )
            binding.toolbar.setTitleTextColor(toolbarContentsColor)

            // Support display cutouts
            val navHeaderOffsetView =
                binding.navView.getHeaderView(0).findViewById<View>(R.id.offset_view)
            ViewCompat.setOnApplyWindowInsetsListener(navHeaderOffsetView) { view, windowInsets ->
                view.layoutParams.apply {
                    height = windowInsets.systemWindowInsetTop
                }

                windowInsets
            }
        }
        binding.navViewFooter.setOnClickListener {
            activityActionCreator.openUrl("https://goo.gl/forms/FIb5p75kN4X3WY7d2")
        }
    }

    private fun setupStatusBarColors() {
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                statusBarColors.drawerSlideOffset = slideOffset
            }
        })

        statusBarColors.systemUiVisibility.changed(this) { visibility ->
            window.decorView.systemUiVisibility = visibility
        }
        statusBarColors.statusBarColor.changed(this) { color ->
            window.statusBarColor = color
        }
    }

    private fun setupNavigationView() {
        binding.navView.setNavigationItemSelectedListener { item ->
            // drawer close
            if (binding.drawerLayout.isDrawerOpen(binding.navView)) {
                binding.drawerLayout.closeDrawer(binding.navView)
            }
            // navigate
            val handled = handleNavigation(item.itemId)
            // check current displayed item in navigation menu / uncheck others
            checkCurrentDestinationIdInDrawer(item.itemId)

            return@setNavigationItemSelectedListener handled
        }
        binding.navView.doOnLayout {
            // when rotate occur, check drawer menu only displayed page
            checkCurrentDestinationIdInDrawer(navController.currentDestination?.id ?: R.id.main)
        }
    }

    private fun checkCurrentDestinationIdInDrawer(id: Int) {
        var contain = false
        binding.navView.menu.children.forEach {
            val match = it.itemId == id
            it.isChecked = match
            contain = contain or match
        }
        // if id dose not exist in menu items, check main instead (e.g. session detail is displayed)
        if (!contain) {
            binding.navView.menu.findItem(R.id.main).isChecked = true
        }
    }

    private fun handleNavigation(@IdRes itemId: Int): Boolean {
        return try {
            // ignore if current destination is selected
            if (navController.currentDestination?.id == itemId) return false
            val builder = NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setPopUpTo(R.id.main, false)
            val options = builder.build()
            navController.navigate(itemId, null, options)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return handleNavigation(item.itemId) || super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            return
        }
        super.onBackPressed()
    }
}

@Module
abstract class MainActivityModule {
    @Binds abstract fun providesActivity(mainActivity: MainActivity): FragmentActivity

    @PageScope
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
    @ContributesAndroidInjector(
        modules = [SearchFragmentModule::class, SessionAssistedInjectModule::class]
    )
    abstract fun contributeSearchFragment(): SearchFragment

    @PageScope
    @ContributesAndroidInjector(
        modules = [TabularFromSessionPagesFragmentModule::class, SessionAssistedInjectModule::class]
    )
    abstract fun contributeTabularFormSessionPagesFragment(): TabularFormSessionPagesFragment

    @PageScope
    @ContributesAndroidInjector(
        modules = [AboutFragmentModule::class]
    )
    abstract fun contributeAboutFragment(): AboutFragment

    @PageScope
    @ContributesAndroidInjector(modules = [AnnouncementFragmentModule::class])
    abstract fun contributeAnnouncementFragment(): AnnouncementFragment

    @PageScope
    @ContributesAndroidInjector(modules = [FloorMapFragmentModule::class])
    abstract fun contributeFloorMapFragment(): FloorMapFragment

    @PageScope
    @ContributesAndroidInjector(modules = [SponsorFragmentModule::class])
    abstract fun contributeSponsorFragment(): SponsorFragment

    @PageScope
    @ContributesAndroidInjector(modules = [SessionSurveyFragmentModule::class])
    abstract fun contributeSessionSurveyFragment(): SessionSurveyFragment

    @PageScope
    @ContributesAndroidInjector(modules = [StaffSearchFragmentModule::class])
    abstract fun contributeStaffSearchFragment(): StaffSearchFragment

    @PageScope
    @ContributesAndroidInjector(
        modules = [ContributorFragmentModule::class, ContributorAssistedInjectModule::class]
    )
    abstract fun contrbutorContributorFragment(): ContributorFragment

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
