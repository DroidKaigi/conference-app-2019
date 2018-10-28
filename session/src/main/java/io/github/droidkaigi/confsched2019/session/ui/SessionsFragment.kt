package io.github.droidkaigi.confsched2019.session.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.Navigation
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.ViewHolder
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.ext.android.changed
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.SessionTab
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.FragmentSessionFilterBinding
import io.github.droidkaigi.confsched2019.session.databinding.FragmentSessionsBinding
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.SessionActionCreator
import io.github.droidkaigi.confsched2019.session.ui.item.SessionItem
import io.github.droidkaigi.confsched2019.session.ui.store.AllSessionsStore
import io.github.droidkaigi.confsched2019.session.ui.store.SessionStore
import io.github.droidkaigi.confsched2019.ui.BottomSheetBehavior
import io.github.droidkaigi.confsched2019.ui.DaggerFragment
import io.github.droidkaigi.confsched2019.ui.MainFragmentDirections
import me.tatarka.injectedvmprovider.InjectedViewModelProviders
import javax.inject.Inject
import javax.inject.Provider

class SessionsFragment : DaggerFragment() {
    lateinit var binding: FragmentSessionFilterBinding

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_session_filter, container,
            false)
        return binding.root
    }

    private val args: SessionsFragmentArgs by lazy {
        SessionsFragmentArgs.fromBundle(arguments)
    }

    private val bottomSheetBehavior: BottomSheetBehavior<*>
        get() = BottomSheetBehavior.from(binding.sessionSheet)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState == null) {
            val fragment: Fragment = when (val tab = SessionTab.tabs[args.tabIndex]) {
                is SessionTab.Day -> {
                    BottomSheetDaySessionsFragment.newInstance(
                        BottomSheetDaySessionsFragmentArgs
                            .Builder(tab.day)
                            .build())
                }
                SessionTab.Favorite -> {
                    FavoriteSessionsFragment.newInstance()
                }
            }

            childFragmentManager
                .beginTransaction()
                .replace(R.id.session_sheet, fragment)
                .commit()
        }
        bottomSheetBehavior.isHideable = false
        binding.sessionSheet.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                binding.sessionSheet.viewTreeObserver.removeOnPreDrawListener(this)
                if (isDetached) return false
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

                return false
            }
        })
        allSessionsStore.selectedTab.changed(viewLifecycleOwner) {
            if (SessionTab.tabs[args.tabIndex] == it) {
                bottomSheetBehavior.isHideable = false
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    companion object {
        fun newInstance(args: SessionsFragmentArgs): SessionsFragment {
            return SessionsFragment().apply {
                arguments = args.toBundle()
            }
        }
    }
}

class BottomSheetDaySessionsFragment : DaggerFragment() {
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
        val daySessionsFragmentArgs = BottomSheetDaySessionsFragmentArgs.fromBundle(arguments)

        allSessionsStore.daySessions(daySessionsFragmentArgs.day).changed(this) { sessions ->
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
        fun newInstance(args: BottomSheetDaySessionsFragmentArgs): BottomSheetDaySessionsFragment {
            return BottomSheetDaySessionsFragment().apply {
                arguments = args.toBundle()
            }
        }
    }
}

@Module
object DaySessionsFragmentModule {
    @JvmStatic @Provides
    fun providesLifecycle(
        sessionsFragmentBottomSheet: BottomSheetDaySessionsFragment): LifecycleOwner {
        return sessionsFragmentBottomSheet.viewLifecycleOwner
    }
}
