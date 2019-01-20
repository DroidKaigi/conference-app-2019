package io.github.droidkaigi.confsched2019.session.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.model.SessionPage
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.ui.widget.DaggerFragment
import io.github.droidkaigi.confsched2019.session.databinding.FragmentTabularFormSessionPagesBinding
import io.github.droidkaigi.confsched2019.session.di.SessionPageScope
import io.github.droidkaigi.confsched2019.session.ui.store.SessionContentsStore
import javax.inject.Inject

class TabularFormSessionPagesFragment : DaggerFragment() {

    private lateinit var binding: FragmentTabularFormSessionPagesBinding
    @Inject lateinit var sessionContentsStore: SessionContentsStore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_tabular_form_session_pages,
            container,
            false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupSessionPager()
    }

    private fun setupSessionPager() {
        binding.tabularFormSessionsTabLayout.setupWithViewPager(
            binding.tabularFormSessionsViewpager
        )
        binding.tabularFormSessionsViewpager.adapter = object : FragmentStatePagerAdapter(
            childFragmentManager
        ) {

            private val days = listOf(SessionPage.pageOfDay(1), SessionPage.pageOfDay(2))

            override fun getItem(position: Int): Fragment {
                return TabularFormSessionPageFragment.newInstance()
            }

            override fun getPageTitle(position: Int) = days[position].title
            override fun getCount(): Int = days.size
        }
    }
}

@Module
abstract class TabularFromSessionPagesFragmentModule {

    @SessionPageScope
    @ContributesAndroidInjector(modules = [TabularFormSessionPageFragmentModule::class])
    abstract fun contributeTabularFormSessionPageFragment(): TabularFormSessionPageFragment

    @Module
    companion object {
        @JvmStatic @Provides
        @PageScope
        fun providesLifecycle(
            tabularFromSessionPagesFragment: TabularFormSessionPagesFragment
        ): Lifecycle {
            return tabularFromSessionPagesFragment.viewLifecycleOwner.lifecycle
        }
    }
}
