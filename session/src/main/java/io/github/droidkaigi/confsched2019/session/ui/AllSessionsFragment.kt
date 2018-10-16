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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dagger.Binds
import dagger.Module
import dagger.android.support.DaggerFragment
import io.github.droidkaigi.confsched2019.ext.android.changed
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.FragmentAllSessionsBinding
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.SessionActionCreator
import io.github.droidkaigi.confsched2019.session.ui.store.AllSessionsStore
import io.github.droidkaigi.confsched2019.session.ui.store.SessionStore
import io.github.droidkaigi.confsched2019.util.ProgressTimeLatch
import javax.inject.Inject

class AllSessionsFragment : DaggerFragment() {
    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject lateinit var sessionActionCreator: SessionActionCreator

    lateinit var binding: FragmentAllSessionsBinding

    private lateinit var progressTimeLatch: ProgressTimeLatch

    @Inject lateinit var sessionStore: SessionStore

    private val allSessionsStore: AllSessionsStore by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(AllSessionsStore::class.java)
    }

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
            override fun fragment() = DaySessionsFragment.newInstance(1)
        },
        Day2 {
            override fun title() = "Day2"
            override fun fragment() = DaySessionsFragment.newInstance(1)
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
interface AllSessionsFragmentModule {
    @Binds
    fun providesLifecycle(allSessionsFragment: AllSessionsFragment): LifecycleOwner {
        return allSessionsFragment.viewLifecycleOwner
    }
}
