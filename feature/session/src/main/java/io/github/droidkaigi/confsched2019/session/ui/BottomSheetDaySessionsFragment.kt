package io.github.droidkaigi.confsched2019.session.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.ViewHolder
import io.github.droidkaigi.confsched2019.ext.android.changed
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.FragmentSessionsBinding
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.AllSessionActionCreator
import io.github.droidkaigi.confsched2019.session.ui.item.SessionItem
import io.github.droidkaigi.confsched2019.session.ui.store.AllSessionsStore
import io.github.droidkaigi.confsched2019.session.ui.widget.DaggerFragment
import io.github.droidkaigi.confsched2019.session.ui.widget.SessionsItemDecoration
import me.tatarka.injectedvmprovider.InjectedViewModelProviders
import javax.inject.Inject
import javax.inject.Provider

class BottomSheetDaySessionsFragment : DaggerFragment() {
    lateinit var binding: FragmentSessionsBinding

    @Inject lateinit var allSessionActionCreator: AllSessionActionCreator
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
        allSessionActionCreator.toggleFavorite(clickedSession)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.allSessionsRecycler.adapter = groupAdapter
        binding.allSessionsRecycler.addItemDecoration(
            SessionsItemDecoration(resources, groupAdapter)
        )

        val daySessionsFragmentArgs = BottomSheetDaySessionsFragmentArgs.fromBundle(arguments)

        allSessionsStore.daySessions(daySessionsFragmentArgs.day).changed(this) { sessions ->
            val items = sessions.filterIsInstance<Session.SpeechSession>()
                .map { session ->
                    SessionItem(
                        speechSession = session,
                        onFavoriteClickListener = onFavoriteClickListener,
                        onClickListener = { clickedSession ->
                            Navigation
                                .findNavController(requireActivity(), R.id.root_nav_host_fragment)
                                .navigate(
                                    AllSessionsFragmentDirections.actionSessionToSessionDetail(
                                        clickedSession.id
                                    )
                                )
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
