package io.github.droidkaigi.confsched2019.session.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.ViewHolder
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.ext.android.changed
import io.github.droidkaigi.confsched2019.model.Room
import io.github.droidkaigi.confsched2019.model.ServiceSession
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.SpeechSession
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.FragmentTabularFormSessionPageBinding
import io.github.droidkaigi.confsched2019.session.di.SessionPageScope
import io.github.droidkaigi.confsched2019.session.ui.item.TabularServiceSessionItem
import io.github.droidkaigi.confsched2019.session.ui.item.TabularSpeechSessionItem
import io.github.droidkaigi.confsched2019.session.ui.store.SessionPagesStore
import io.github.droidkaigi.confsched2019.session.ui.widget.DaggerFragment
import io.github.droidkaigi.confsched2019.session.ui.widget.TimeTableLayoutManager
import me.tatarka.injectedvmprovider.InjectedViewModelProviders
import javax.inject.Inject
import javax.inject.Provider

class TabularFormSessionPageFragment : DaggerFragment() {

    private lateinit var binding: FragmentTabularFormSessionPageBinding

    @Inject lateinit var sessionPagesStoreProvider: Provider<SessionPagesStore>
    private val sessionPagesStore: SessionPagesStore by lazy {
        InjectedViewModelProviders.of(requireActivity()).get(sessionPagesStoreProvider)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_tabular_form_session_page,
            container,
            false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val groupAdapter = GroupAdapter<ViewHolder<*>>()
        binding.tabularFormSessionsRecycler.apply {
            adapter = groupAdapter
            layoutManager = TimeTableLayoutManager(
                resources.getDimensionPixelSize(R.dimen.tabular_form_column_width),
                resources.getDimensionPixelSize(R.dimen.tabular_form_px_per_minute)
            ) { position ->
                val item = groupAdapter.getItem(position)
                val session = when (item) {
                    is TabularSpeechSessionItem -> item.session as Session
                    is TabularServiceSessionItem -> item.session as Session
                    else -> throw IllegalStateException() // TODO: Fill with Spacer
                }
                TimeTableLayoutManager.PeriodInfo(
                    session.startTime.unixMillisLong,
                    session.endTime.unixMillisLong,
                    session.room.sequentialNumber
                )
            }
        }

        sessionPagesStore.sessionsByDay(arguments?.getInt(KEY_DAY) ?: 1) // TODO: SafeArgs
            .changed(viewLifecycleOwner) { sessions ->
                sessions.map { session ->
                    when (session) {
                        is SpeechSession -> TabularSpeechSessionItem(session)
                        is ServiceSession -> TabularServiceSessionItem(session)
                    }
                }.let(groupAdapter::update)
            }
    }

    private val Room.sequentialNumber: Int
        get() {
            return when (id) {
                3869 -> 0
                3870 -> 1
                3871 -> 2
                3872 -> 3
                3873 -> 4
                3874 -> 5
                3959 -> 6
                3875 -> 7
                3876 -> 8
                else -> 9
            }
        }

    companion object {
        // TODO: use SageArgs
        const val KEY_DAY = "day"

        fun newInstance(day: Int): TabularFormSessionPageFragment {
            return TabularFormSessionPageFragment()
                .apply { arguments = Bundle().apply { putInt(KEY_DAY, day) } }
        }
    }
}

@Module
abstract class TabularFormSessionPageFragmentModule {

    @Module
    companion object {
        @JvmStatic
        @Provides
        @SessionPageScope
        fun providesLifecycle(
            tabularFromSessionPageFragment: TabularFormSessionPageFragment
        ): Lifecycle {
            return tabularFromSessionPageFragment.viewLifecycleOwner.lifecycle
        }
    }
}
