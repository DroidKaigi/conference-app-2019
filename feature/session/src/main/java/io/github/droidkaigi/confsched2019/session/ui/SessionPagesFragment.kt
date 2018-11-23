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
import com.shopify.livedataktx.observe
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import io.github.droidkaigi.confsched2019.ext.android.changed
import io.github.droidkaigi.confsched2019.model.SessionTab
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.FragmentSessionPagesBinding
import io.github.droidkaigi.confsched2019.session.di.AllSessionsScope
import io.github.droidkaigi.confsched2019.session.di.SessionAssistedInjectModule
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.SessionPagesActionCreator
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.SessionsActionCreator
import io.github.droidkaigi.confsched2019.session.ui.store.SessionPagesStore
import io.github.droidkaigi.confsched2019.session.ui.store.SessionsStore
import io.github.droidkaigi.confsched2019.session.ui.widget.DaggerFragment
import io.github.droidkaigi.confsched2019.user.store.UserStore
import io.github.droidkaigi.confsched2019.util.ProgressTimeLatch
import me.tatarka.injectedvmprovider.InjectedViewModelProviders
import javax.inject.Inject
import javax.inject.Provider

class SessionPagesFragment : DaggerFragment() {

    lateinit var binding: FragmentSessionPagesBinding

    @Inject lateinit var sessionsActionCreator: SessionsActionCreator
    @Inject lateinit var sessionsStore: SessionsStore

    @Inject lateinit var sessionPagesStoreProvider: Provider<SessionPagesStore>
    private val sessionPagesStore: SessionPagesStore by lazy {
        InjectedViewModelProviders.of(requireActivity())[sessionPagesStoreProvider]
    }
    @Inject lateinit var sessionPagesActionCreator: SessionPagesActionCreator

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
        sessionPagesStore.filtersChange.observe(viewLifecycleOwner) {
            if (userStore.logined.value == true) {
                sessionPagesActionCreator.load(sessionPagesStore.filters)
            }
        }

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

            override fun getPageTitle(position: Int) = SessionTab.tabs[position].title
            override fun getCount(): Int = SessionTab.tabs.size
        }
        binding.sessionsViewpager.addOnPageChangeListener(
            object : ViewPager.SimpleOnPageChangeListener() {
                override fun onPageSelected(position: Int) {
                    sessionPagesActionCreator.selectTab(SessionTab.tabs[position])
                }
            }
        )
        progressTimeLatch = ProgressTimeLatch { showProgress ->
            binding.progressBar.isVisible = showProgress
        }.apply {
            loading = true
        }

        fun applyLoadingState() {
            progressTimeLatch.loading = sessionsStore.isLoading || sessionPagesStore.isLoading
        }
        sessionsStore.loadingState.changed(this) {
            applyLoadingState()
        }
        sessionPagesStore.loadingState.changed(this) {
            applyLoadingState()
        }
    }
}

@Module
abstract class AllSessionsFragmentModule {

    @ContributesAndroidInjector(modules = [SessionFilterFragmentModule::class])
    abstract fun contributeSessionFilterFragment(): SessionPageFragment

    @ContributesAndroidInjector(
        modules = [SessionFilterFragmentModule::class, SessionAssistedInjectModule::class]
    )
    abstract fun contributeBottomSheetDaySessionsFragment(): BottomSheetDaySessionsFragment

    @ContributesAndroidInjector(
        modules = [FavoriteSessionsFragmentModule::class, SessionAssistedInjectModule::class]
    )
    abstract fun contributeFavoriteSessionsFragment(): BottomSheetFavoriteSessionsFragment

    @Module
    companion object {
        @AllSessionsScope @JvmStatic @Provides fun providesLifecycle(
            sessionPagesFragment: SessionPagesFragment
        ): Lifecycle {
            return sessionPagesFragment.viewLifecycleOwner.lifecycle
        }
    }
}
