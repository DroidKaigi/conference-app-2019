package io.github.droidkaigi.confsched2019.session.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager.widget.ViewPager
import com.google.android.material.chip.Chip
import com.shopify.livedataktx.observe
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.ext.android.changed
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.SessionPage
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.FragmentSessionPagesBinding
import io.github.droidkaigi.confsched2019.session.di.SessionPagesScope
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.SessionsActionCreator
import io.github.droidkaigi.confsched2019.session.ui.store.SessionsStore
import io.github.droidkaigi.confsched2019.session.ui.widget.DaggerFragment
import io.github.droidkaigi.confsched2019.user.store.UserStore
import io.github.droidkaigi.confsched2019.util.ProgressTimeLatch
import javax.inject.Inject

class SessionPagesFragment : DaggerFragment() {

    private lateinit var binding: FragmentSessionPagesBinding

    @Inject lateinit var sessionsActionCreator: SessionsActionCreator
    @Inject lateinit var sessionsStore: SessionsStore
    @Inject lateinit var userStore: UserStore

    private lateinit var progressTimeLatch: ProgressTimeLatch

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
            binding.progressBar.isVisible = showProgress
        }.apply {
            loading = true
        }

        userStore.registered.changed(viewLifecycleOwner) { registered ->
            // Now, registered, we can load sessions
            if (registered && sessionsStore.isInitialized) {
                sessionsActionCreator.refresh()
            }
        }
        sessionsStore.filtersChange.observe(viewLifecycleOwner) {
            if (sessionsStore.isLoaded) {
                sessionsActionCreator.load(sessionsStore.filters)
            }
        }
        sessionsStore.loadingState.changed(viewLifecycleOwner) {
            progressTimeLatch.loading = it == LoadingState.LOADING
        }
    }

    private fun setupSessionPager() {
        binding.sessionsTabLayout.setupWithViewPager(binding.sessionsViewpager)
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
                    sessionsActionCreator.selectTab(SessionPage.pages[position])
                }
            }
        )

        (0 until binding.sessionsTabLayout.tabCount).forEach {
            val view = layoutInflater.inflate(
                R.layout.layout_title_chip, binding.sessionsTabLayout, false
            ) as ViewGroup
            val chip = view.getChildAt(0) as Chip
            val tab = binding.sessionsTabLayout.getTabAt(it)
            tab?.let {
                chip.text = tab.text
                tab.setCustomView(view)
            }
        }
    }
}

@Module
abstract class SessionPagesFragmentModule {

    @PageScope
    @ContributesAndroidInjector(modules = [SessionPageFragmentModule::class])
    abstract fun contributeSessionPageFragment(): SessionPageFragment

    @Module
    companion object {
        @SessionPagesScope @JvmStatic @Provides fun providesLifecycle(
            sessionPagesFragment: SessionPagesFragment
        ): Lifecycle {
            return sessionPagesFragment.viewLifecycleOwner.lifecycle
        }
    }
}
