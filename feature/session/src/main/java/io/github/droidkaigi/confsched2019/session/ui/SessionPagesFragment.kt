package io.github.droidkaigi.confsched2019.session.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.shopify.livedataktx.observe
import com.soywiz.klock.DateTime
import com.soywiz.klock.hours
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.ext.android.changed
import io.github.droidkaigi.confsched2019.model.SessionPage
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.FragmentSessionPagesBinding
import io.github.droidkaigi.confsched2019.session.di.SessionPageScope
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.SessionContentsActionCreator
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.SessionPagesActionCreator
import io.github.droidkaigi.confsched2019.session.ui.store.SessionContentsStore
import io.github.droidkaigi.confsched2019.session.ui.store.SessionPagesStore
import io.github.droidkaigi.confsched2019.session.ui.widget.DaggerFragment
import io.github.droidkaigi.confsched2019.user.store.UserStore
import io.github.droidkaigi.confsched2019.util.ProgressTimeLatch
import me.tatarka.injectedvmprovider.InjectedViewModelProviders
import javax.inject.Inject
import javax.inject.Provider

class SessionPagesFragment : DaggerFragment() {

    private lateinit var binding: FragmentSessionPagesBinding

    @Inject lateinit var sessionPagesActionCreator: SessionPagesActionCreator
    @Inject lateinit var sessionContentsActionCreator: SessionContentsActionCreator
    @Inject lateinit var sessionContentsStore: SessionContentsStore
    @Inject lateinit var userStore: UserStore
    @Inject lateinit var sessionPagesStoreProvider: Provider<SessionPagesStore>
    private val sessionPagesStore: SessionPagesStore by lazy {
        InjectedViewModelProviders.of(requireActivity()).get(sessionPagesStoreProvider)
    }

    private lateinit var progressTimeLatch: ProgressTimeLatch

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_session_pages,
            container,
            false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupSessionPager()

        progressTimeLatch = ProgressTimeLatch { showProgress ->
            binding.sessionsProgressBar.isVisible = showProgress
        }.apply {
            loading = true
        }

        userStore.registered.changed(viewLifecycleOwner) { registered ->
            // Now, registered, we can load sessions
            if (registered && sessionContentsStore.isInitialized) {
                sessionContentsActionCreator.refresh()
            }
        }
        sessionContentsStore.sessionContents.changed(viewLifecycleOwner) { sessionContents ->
            sessionPagesActionCreator.load(sessionContentsStore.sessions)
        }
        sessionContentsStore.loadingState.changed(viewLifecycleOwner) { loadingState ->
            progressTimeLatch.loading = loadingState.isLoading
        }
        sessionPagesStore.sessionScrollAdjusted.observe(viewLifecycleOwner) {
            // just observe
        }
    }

    override fun onDestroyView() {
        binding.sessionsTabLayout.setupWithViewPager(null)
        super.onDestroyView()
    }

    private fun setupSessionPager() {
        binding.sessionsTabLayout.setupWithViewPager(binding.sessionsViewpager)
        binding.sessionsViewpager.pageMargin =
            resources.getDimensionPixelSize(R.dimen.session_pager_horizontal_padding)
        binding.sessionsViewpager.adapter = object : FragmentStatePagerAdapter(
            childFragmentManager
        ) {
            override fun getItem(position: Int): Fragment {
                return SessionPageFragment.newInstance(
                    SessionPageFragmentArgs
                        .Builder(position)
                        .build()
                )
            }

            override fun getPageTitle(position: Int) = SessionPage.pages[position].title
            override fun getCount(): Int = SessionPage.pages.size
        }
        binding.sessionsViewpager.addOnPageChangeListener(
            object : ViewPager.SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    sessionPagesActionCreator.selectTab(SessionPage.pages[position])
                }
            }
        )

        binding.sessionsTabLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {
                    tab?.let {
                        sessionPagesActionCreator.reselectTab(SessionPage.pages[it.position])
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) { // no-op
                }

                override fun onTabSelected(tab: TabLayout.Tab?) { // no-op
                }
            })

        (0 until binding.sessionsTabLayout.tabCount).forEach { tabIndex ->
            val tab = binding.sessionsTabLayout.getTabAt(tabIndex)
            tab?.let {
                val view = layoutInflater.inflate(
                    R.layout.layout_tab_item, binding.sessionsTabLayout, false
                ) as? TextView
                view?.let { textView ->
                    textView.text = tab.text
                    it.customView = textView
                }
            }
        }

        val jstNow = DateTime.now().toOffset(9.hours)
        if (jstNow.yearInt == 2019 && jstNow.month1 == 2 && jstNow.dayOfMonth == 8) {
            binding.sessionsViewpager.currentItem = 1
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_toolbar, menu)
    }
}

@Module
abstract class SessionPagesFragmentModule {

    @SessionPageScope
    @ContributesAndroidInjector(modules = [SessionPageFragmentModule::class])
    abstract fun contributeSessionPageFragment(): SessionPageFragment

    @Module
    companion object {
        @PageScope @JvmStatic @Provides fun providesLifecycle(
            sessionPagesFragment: SessionPagesFragment
        ): Lifecycle {
            return sessionPagesFragment.viewLifecycleOwner.lifecycle
        }
    }
}
