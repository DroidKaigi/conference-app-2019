package io.github.droidkaigi.confsched2019.session.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dagger.Binds
import dagger.Module
import dagger.android.support.DaggerFragment
import io.github.droidkaigi.confsched2019.ext.android.changed
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.FragmentSessionDetailBinding
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.SessionActionCreator
import io.github.droidkaigi.confsched2019.session.ui.store.AllSessionsStore
import io.github.droidkaigi.confsched2019.session.ui.store.SessionStore
import javax.inject.Inject

class SessionDetailFragment : DaggerFragment() {

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject lateinit var sessionActionCreator: SessionActionCreator

    lateinit var binding: FragmentSessionDetailBinding

    @Inject lateinit var sessionStore: SessionStore

    private val allSessionsStore: AllSessionsStore by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(AllSessionsStore::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_session_detail, container,
            false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val sessionId = arguments?.getString(EXTRA_SESSION) ?: ""
        binding.favorite.setOnClickListener {
            val session = sessionStore.session(sessionId).value ?: return@setOnClickListener
            sessionActionCreator.toggleFavorite(session)
        }
        sessionStore.session(sessionId).changed(this) { session ->
            binding.session = session
        }
    }

    companion object {
        const val EXTRA_SESSION = "session"
        fun newInstance(day: Int): SessionDetailFragment {
            return SessionDetailFragment().apply {
                arguments = Bundle().apply { putInt(EXTRA_SESSION, day) }
            }
        }
    }
}

@Module
interface SessionDetailFragmentModule {
    @Binds
    fun providesLifecycle(sessionsFragment: SessionDetailFragment): LifecycleOwner {
        return sessionsFragment.viewLifecycleOwner
    }
}
