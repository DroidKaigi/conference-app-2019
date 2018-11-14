package io.github.droidkaigi.confsched2019.session.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.ViewHolder
import io.github.droidkaigi.confsched2019.ext.android.changed
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.FragmentBottomSheetSessionsBinding
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.SessionPagesActionCreator
import io.github.droidkaigi.confsched2019.session.ui.item.SessionItem
import io.github.droidkaigi.confsched2019.session.ui.store.SessionPagesStore
import io.github.droidkaigi.confsched2019.session.ui.widget.DaggerFragment
import io.github.droidkaigi.confsched2019.session.ui.widget.SessionsItemDecoration
import me.tatarka.injectedvmprovider.InjectedViewModelProviders
import javax.inject.Inject
import javax.inject.Provider

class BottomSheetDaySessionsFragment : DaggerFragment() {
    lateinit var binding: FragmentBottomSheetSessionsBinding

    @Inject lateinit var sessionPagesActionCreator: SessionPagesActionCreator
    @Inject lateinit var sessionPagesStoreProvider: Provider<SessionPagesStore>
    @Inject lateinit var sessionItemFactory: SessionItem.Factory

    private val sessionPagesStore: SessionPagesStore by lazy {
        InjectedViewModelProviders.of(requireActivity())[sessionPagesStoreProvider]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_bottom_sheet_sessions, container, false
        )
        return binding.root
    }

    private val groupAdapter = GroupAdapter<ViewHolder<*>>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.allSessionsRecycler.adapter = groupAdapter
        binding.allSessionsRecycler.addItemDecoration(
            SessionsItemDecoration(resources, groupAdapter)
        )

        val daySessionPageFragmentArgs = BottomSheetDaySessionsFragmentArgs.fromBundle(arguments)

        sessionPagesStore.daySessions(daySessionPageFragmentArgs.day).changed(this) { sessions ->
            val items = sessions.filterIsInstance<Session.SpeechSession>()
                .map { session ->
                    sessionItemFactory.create(session, sessionPagesStore)
                }
            groupAdapter.update(items)
        }
    }

    companion object {
        fun newInstance(
            args: BottomSheetDaySessionsFragmentArgs
        ): BottomSheetDaySessionsFragment {
            return BottomSheetDaySessionsFragment().apply {
                arguments = args.toBundle()
            }
        }
    }
}
