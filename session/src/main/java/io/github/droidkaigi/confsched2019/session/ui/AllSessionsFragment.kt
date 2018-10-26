package io.github.droidkaigi.confsched2019.session.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.LifecycleOwner
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.ext.android.changed
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.FragmentAllSessionsBinding
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.AllSessionActionCreator
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.SessionActionCreator
import io.github.droidkaigi.confsched2019.session.ui.store.AllSessionsStore
import io.github.droidkaigi.confsched2019.session.ui.store.SessionStore
import io.github.droidkaigi.confsched2019.ui.DaggerFragment
import io.github.droidkaigi.confsched2019.util.ProgressTimeLatch
import me.tatarka.injectedvmprovider.InjectedViewModelProviders
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class AllSessionsFragment : DaggerFragment() {

    lateinit var binding: FragmentAllSessionsBinding

    @Inject lateinit var sessionActionCreator: SessionActionCreator
    @Inject lateinit var allSessionActionCreator: AllSessionActionCreator
    @Inject lateinit var sessionStore: SessionStore

    @Inject lateinit var allSessionsStoreProvider: Provider<AllSessionsStore>
    private val allSessionsStore: AllSessionsStore by lazy {
        InjectedViewModelProviders.of(requireActivity())[allSessionsStoreProvider]
    }

    private lateinit var progressTimeLatch: ProgressTimeLatch

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_all_sessions,
            container,
            false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        allSessionActionCreator.load()
        binding.sessionsTabLayout.setupWithViewPager(binding.sessionsViewpager)
        binding.sessionsViewpager.adapter = object : FragmentStatePagerAdapter(
            childFragmentManager
        ) {
            override fun getItem(position: Int) = Tab.values()[position].fragment()
            override fun getPageTitle(position: Int) = Tab.values()[position].title()
            override fun getCount(): Int = Tab.values().size
        }
        progressTimeLatch = ProgressTimeLatch { showProgress ->
            binding.progressBar.isVisible = showProgress
        }.apply {
            loading = true
        }
        sessionStore.loadingState.changed(this) {
            progressTimeLatch.loading = it == LoadingState.LOADING
        }
    }

    enum class Tab {
        Day1 {
            override fun title() = "Day1"
            override fun fragment(): DaySessionsFragment {
                return DaySessionsFragment.newInstance(DaySessionsFragmentArgs
                    .Builder(1)
                    .build())
            }
        },
        Day2 {
            override fun title() = "Day2"
            override fun fragment() = DaySessionsFragment.newInstance(DaySessionsFragmentArgs
                .Builder(2)
                .build())
        },
        Favorite {
            override fun title() = "Favorite"
            override fun fragment() = FavoriteSessionsFragment.newInstance()
        };

        abstract fun title(): String
        abstract fun fragment(): Fragment
    }

}

@Module
abstract class AllSessionsFragmentModule {
    @Module
    companion object {
        @Named("AllSessionsFragment") @JvmStatic @Provides fun providesLifecycle(
            allSessionsFragment: AllSessionsFragment
        ): LifecycleOwner {
            return allSessionsFragment.viewLifecycleOwner
        }
    }
}
