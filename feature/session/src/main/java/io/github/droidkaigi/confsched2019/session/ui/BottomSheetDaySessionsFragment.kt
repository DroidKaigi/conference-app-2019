package io.github.droidkaigi.confsched2019.session.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.shopify.livedataktx.observe
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.ViewHolder
import io.github.droidkaigi.confsched2019.ext.android.changed
import io.github.droidkaigi.confsched2019.model.ServiceSession
import io.github.droidkaigi.confsched2019.model.Session
import io.github.droidkaigi.confsched2019.model.SessionPage
import io.github.droidkaigi.confsched2019.model.SpeechSession
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.FragmentBottomSheetSessionsBinding
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.SessionContentsActionCreator
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.SessionPageActionCreator
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.SessionPagesActionCreator
import io.github.droidkaigi.confsched2019.session.ui.item.ServiceSessionItem
import io.github.droidkaigi.confsched2019.session.ui.item.SessionItem
import io.github.droidkaigi.confsched2019.session.ui.item.SpeechSessionItem
import io.github.droidkaigi.confsched2019.session.ui.store.SessionContentsStore
import io.github.droidkaigi.confsched2019.session.ui.store.SessionPageStore
import io.github.droidkaigi.confsched2019.session.ui.store.SessionPagesStore
import io.github.droidkaigi.confsched2019.session.ui.widget.DaggerFragment
import io.github.droidkaigi.confsched2019.session.ui.widget.SessionsItemDecoration
import io.github.droidkaigi.confsched2019.widget.BottomSheetBehavior
import me.tatarka.injectedvmprovider.InjectedViewModelProviders
import me.tatarka.injectedvmprovider.ktx.injectedViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

class BottomSheetDaySessionsFragment : DaggerFragment() {
    private lateinit var binding: FragmentBottomSheetSessionsBinding

    @Inject lateinit var sessionContentsActionCreator: SessionContentsActionCreator
    @Inject lateinit var sessionContentsStore: SessionContentsStore
    @Inject lateinit var sessionPageActionCreator: SessionPageActionCreator
    @Inject lateinit var sessionPagesActionCreator: SessionPagesActionCreator
    @Inject lateinit var sessionPageFragmentProvider: Provider<SessionPageFragment>
    @Inject lateinit var speechSessionItemFactory: SpeechSessionItem.Factory
    @Inject lateinit var sessionPageStoreFactory: SessionPageStore.Factory
    @Inject lateinit var serviceSessionItemFactory: ServiceSessionItem.Factory
    private val sessionPageStore: SessionPageStore by lazy {
        sessionPageFragmentProvider.get().injectedViewModelProvider
            .get(SessionPageStore::class.java.name) {
                sessionPageStoreFactory.create(SessionPage.pages[args.day])
            }
    }
    @Inject lateinit var sessionPagesStoreProvider: Provider<SessionPagesStore>
    val sessionPagesStore: SessionPagesStore by lazy {
        InjectedViewModelProviders.of(requireActivity()).get(sessionPagesStoreProvider)
    }

    private val args: BottomSheetDaySessionsFragmentArgs by lazy {
        BottomSheetDaySessionsFragmentArgs.fromBundle(arguments ?: Bundle())
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val groupAdapter = GroupAdapter<ViewHolder<*>>()
        binding.sessionsRecycler.apply {
            adapter = groupAdapter
            addItemDecoration(
                SessionsItemDecoration(requireContext(), groupAdapter)
            )
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    binding.sessionsListHeaderShadow.isVisible =
                        recyclerView.canScrollVertically(-1)
                }
            })
        }

        val onFilterButtonClick: (View) -> Unit = {
            sessionPageActionCreator.toggleFilterExpanded(SessionPage.pageOfDay(args.day))
        }
        binding.sessionsBottomSheetShowFilterButton.setOnClickListener(onFilterButtonClick)
        binding.sessionsBottomSheetHideFilterButton.setOnClickListener(onFilterButtonClick)

        sessionPagesStore.filteredSessionsByDay(args.day).changed(viewLifecycleOwner) { sessions ->
            val items = sessions
                .map<Session, Item<*>> { session ->
                    when (session) {
                        is SpeechSession ->
                            speechSessionItemFactory.create(
                                session,
                                SessionPagesFragmentDirections.actionSessionToSessionDetail(
                                    session.id
                                ),
                                SessionPagesFragmentDirections.actionSessionToSessionSurvey(
                                    session
                                ),
                                true
                            )
                        is ServiceSession ->
                            serviceSessionItemFactory.create(
                                session,
                                SessionPagesFragmentDirections.actionSessionToSessionDetail(
                                    session.id
                                ),
                                true
                            )
                    }
                }
            groupAdapter.update(items)

            binding.shouldShowEmptyStateView = false

            val titleText = items
                .asSequence()
                .filterIsInstance<SessionItem>()
                .firstOrNull()
                ?.session
                ?.startDayText ?: return@changed
            binding.sessionsBottomSheetTitle.text = titleText

            if (sessionPagesStore.sessionScrollAdjusted.value == false) {
                scrollToCurrentSession()
                sessionPagesActionCreator.dispatchSessionScrollAdjusted()
            }
        }
        sessionPagesStore.filters.changed(viewLifecycleOwner) {
            binding.isFiltered = it.isFiltered()
        }
        sessionPageStore.filterSheetState.changed(viewLifecycleOwner) { newState ->
            if (newState == BottomSheetBehavior.STATE_EXPANDED ||
                newState == BottomSheetBehavior.STATE_COLLAPSED
            ) {
                TransitionManager.beginDelayedTransition(
                    binding.root as ViewGroup, AutoTransition().apply {
                        excludeChildren(binding.sessionsBottomSheetTitle, true)
                        excludeChildren(binding.sessionsRecycler, true)
                    })
                val isCollapsed = newState == BottomSheetBehavior.STATE_COLLAPSED
                binding.isCollapsed = isCollapsed
            }
        }
        sessionPagesStore.reselectedTab.observe(viewLifecycleOwner) {
            if (SessionPage.pageOfDay(args.day) == it) {
                scrollToCurrentSession()
            }
        }
    }

    override fun onDestroyView() {
        binding.sessionsRecycler.adapter = null
        super.onDestroyView()
    }

    private fun scrollToCurrentSession() {
        val position = sessionPagesStore.filteredSessions.value.orEmpty()
            .filter { session -> session.dayNumber == args.day }
            .indexOfFirst { session -> session.isOnGoing }
        binding.sessionsRecycler.scrollToPosition(position)
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
