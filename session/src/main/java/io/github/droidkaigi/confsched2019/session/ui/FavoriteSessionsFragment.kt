package io.github.droidkaigi.confsched2019.session.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.ViewHolder
import dagger.Binds
import dagger.Module
import dagger.android.support.DaggerFragment
import io.github.droidkaigi.confsched2019.ext.android.changed
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.FragmentSessionsBinding
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.SessionActionCreator
import io.github.droidkaigi.confsched2019.session.ui.item.SessionItem
import io.github.droidkaigi.confsched2019.session.ui.store.AllSessionsStore
import io.github.droidkaigi.confsched2019.session.ui.store.SessionStore
import javax.inject.Inject

class FavoriteSessionsFragment : DaggerFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject lateinit var sessionActionCreator: SessionActionCreator

    lateinit var binding: FragmentSessionsBinding

    @Inject lateinit var sessionStore: SessionStore

    private val allSessionsStore: AllSessionsStore by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(AllSessionsStore::class.java)
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

        sessionStore.favoriteSessions().changed(this) { sessions ->
            val items = sessions
                .map { session ->
                    SessionItem(
                        session = session,
                        onFavoriteClickListener = onFavoriteClickListener,
                        onClickListener = { clickedSession->
                            Navigation
                                .findNavController(requireActivity(), R.id.root_nav_host_fragment)
                                .navigate(R.id.action_session_to_session_detail, Bundle().apply {
                                    putString(SessionDetailFragment.EXTRA_SESSION, clickedSession.id)
                                })
                        }
                    )
                }
            groupAdapter.update(items)
        }
    }

    companion object {
        fun newInstance(): FavoriteSessionsFragment {
            return FavoriteSessionsFragment()
        }
    }
}

@Module
interface FavoriteSessionsFragmentModule {
    @Binds
    fun providesLifecycle(sessionsFragment: FavoriteSessionsFragment): LifecycleOwner {
        return sessionsFragment.viewLifecycleOwner
    }
}
