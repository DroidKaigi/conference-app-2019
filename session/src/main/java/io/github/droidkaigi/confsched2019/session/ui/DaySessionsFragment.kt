package io.github.droidkaigi.confsched2019.session.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.Navigation
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.ViewHolder
import dagger.Module
import dagger.Provides
import dagger.android.support.DaggerFragment
import io.github.droidkaigi.confsched2019.ext.android.changed
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.FragmentSessionsBinding
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.SessionActionCreator
import io.github.droidkaigi.confsched2019.session.ui.item.SessionItem
import io.github.droidkaigi.confsched2019.session.ui.store.AllSessionsStore
import io.github.droidkaigi.confsched2019.session.ui.store.SessionStore
import io.github.droidkaigi.confsched2019.ui.MainFragmentDirections
import me.tatarka.injectedvmprovider.InjectedViewModelProviders
import javax.inject.Inject
import javax.inject.Provider

class DaySessionsFragment : DaggerFragment() {
    lateinit var binding: FragmentSessionsBinding

    @Inject lateinit var sessionActionCreator: SessionActionCreator
    @Inject lateinit var sessionStore: SessionStore

    @Inject lateinit var allSessionsStoreProvider: Provider<AllSessionsStore>
    private val allSessionsStore: AllSessionsStore by lazy {
        InjectedViewModelProviders.of(requireActivity())[allSessionsStoreProvider]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sessions, container, false)
        return binding.root
    }

    private val groupAdapter = GroupAdapter<ViewHolder<*>>()

    private val onFavoriteClickListener = { clickedSession: Session.SpeechSession ->
        sessionActionCreator.toggleFavorite(clickedSession)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.allSessionsRecycler.adapter = groupAdapter

        sessionStore.daySessions(arguments?.getInt(EXTRA_DAY) ?: 0).changed(this) { sessions ->
            val items = sessions.filterIsInstance<Session.SpeechSession>()
                .map { session ->
                    SessionItem(
                        session = session,
                        onFavoriteClickListener = onFavoriteClickListener,
                        onClickListener = { clickedSession ->
                            Navigation
                                .findNavController(requireActivity(), R.id.root_nav_host_fragment)
                                .navigate(MainFragmentDirections.actionSessionToSessionDetail(
                                    clickedSession.id))
                        }
                    )
                }
            groupAdapter.update(items)
        }
    }

    companion object {
        const val EXTRA_DAY = "day"
        fun newInstance(args: DaySessionsFragmentArgs): DaySessionsFragment {
            return DaySessionsFragment().apply {
                arguments = args.toBundle()
            }
        }
    }
}

@Module
object DaySessionsFragmentModule {
    @JvmStatic @Provides
    fun providesLifecycle(sessionsFragment: DaySessionsFragment): LifecycleOwner {
        return sessionsFragment.viewLifecycleOwner
    }
}
