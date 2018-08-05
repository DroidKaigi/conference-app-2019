package io.github.droidkaigi.confsched2019.session.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.ViewHolder
import dagger.android.support.DaggerFragment
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.FragmentAllSessionsBinding
import io.github.droidkaigi.confsched2019.session.model.AllSessionActionCreator
import io.github.droidkaigi.confsched2019.session.model.Session
import io.github.droidkaigi.confsched2019.session.store.AllSessionsStore
import io.github.droidkaigi.confsched2019.session.store.SessionStore
import io.github.droidkaigi.confsched2019.session.ui.item.SessionItem
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import javax.inject.Inject

class AllSessionsFragment : DaggerFragment() {
    val job = Job()

    companion object {
        fun newInstance() = AllSessionsFragment()
    }

    @Inject
    lateinit var sessionStore: SessionStore
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var allSessionActionCreator: AllSessionActionCreator

    lateinit var binding: FragmentAllSessionsBinding

    private val allSessionsStore: AllSessionsStore by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(AllSessionsStore::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate<FragmentAllSessionsBinding>(inflater, R.layout.fragment_all_sessions, container, false)
        return binding.root
    }

    private val groupAdapter = GroupAdapter<ViewHolder<*>>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.allSessionsRecycler.adapter = groupAdapter
        allSessionsStore.sessionsLiveData.observe(this, Observer {
            val list = it.orEmpty()
                    .filterIsInstance<Session.SpeechSession>()
                    .map { SessionItem(it) }
            groupAdapter.update(list)
        })
        allSessionActionCreator.listen(job + UI)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
