package io.github.droidkaigi.confsched2019.session.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import com.google.android.material.tabs.TabLayout
import com.soywiz.klock.DateTime
import com.soywiz.klock.hours
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
    private val days = listOf(SessionPage.pageOfDay(1), SessionPage.pageOfDay(2))
    private var nowTabPosition: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        days.forEach { day ->
            addTabularSessionFragment(day)
        }
    }

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
        setupSessionPages()
    }

    private fun setupSessionPages() {
        // For a few reasons, we stopped using ViewPager in this fragment.
        // See https://github.com/DroidKaigi/conference-app-2019/issues/622 .
        binding.tabularFormSessionsTabLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    nowTabPosition = tab.position
                    val day = days[tab.position]
                    switchShowTabularSessionByDay(day)
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {}

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
            })

        days.forEach { day ->
            val newTab = binding.tabularFormSessionsTabLayout.newTab().apply {
                text = day.title
            }
            binding.tabularFormSessionsTabLayout.addTab(newTab, false)
        }

        nowTabPosition?.let {
            // To make tab display state selected, when back to tabular session
            binding.tabularFormSessionsTabLayout.getTabAt(it)?.select()
        } ?: run {
            val showTabPos = if (isTodayTheSecondDay()) 1 else 0
            binding.tabularFormSessionsTabLayout.getTabAt(showTabPos)?.select()
        }
    }

    private fun addTabularSessionFragment(day: SessionPage.Day) {
        val (id, show) = when (day) {
            SessionPage.Day1 -> Pair(
                R.id.tabular_form_sessions_fragment_container_day1,
                !isTodayTheSecondDay()
            )
            SessionPage.Day2 -> Pair(
                R.id.tabular_form_sessions_fragment_container_day2,
                isTodayTheSecondDay()
            )
            else -> return
        }
        val args = TabularFormSessionPagesFragmentArgs.Builder()
            .setDay(day.day)
            .build()
        val fragment = TabularFormSessionPageFragment.newInstance(args)
        val trans = childFragmentManager.beginTransaction().replace(id, fragment)
        if (!show) {
            trans.hide(fragment)
        }
        trans.commit()
    }

    private fun switchShowTabularSessionByDay(selectedDay: SessionPage.Day) {
        val (showId, hideId) = when (selectedDay) {
            SessionPage.Day1 -> Pair(
                R.id.tabular_form_sessions_fragment_container_day1,
                R.id.tabular_form_sessions_fragment_container_day2
            )
            SessionPage.Day2 -> Pair(
                R.id.tabular_form_sessions_fragment_container_day2,
                R.id.tabular_form_sessions_fragment_container_day1
            )
            else -> return
        }
        val showFragment = childFragmentManager.findFragmentById(showId) ?: return
        val hideFragment = childFragmentManager.findFragmentById(hideId) ?: return
        childFragmentManager.beginTransaction()
            .show(showFragment)
            .hide(hideFragment)
            .commit()
    }

    private fun isTodayTheSecondDay(): Boolean {
        val jstNow = DateTime.now().toOffset(9.hours)
        return jstNow.yearInt == 2019 && jstNow.month1 == 2 && jstNow.dayOfMonth == 8
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
