package io.github.droidkaigi.confsched2019.session.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dagger.android.support.DaggerFragment
import io.github.droidkaigi.confsched2019.R
import io.github.droidkaigi.confsched2019.session.store.AllSessionsStore
import io.github.droidkaigi.confsched2019.session.store.SessionStore
import javax.inject.Inject

class AllSessionsFragment : DaggerFragment() {

    companion object {
        fun newInstance() = AllSessionsFragment()
    }

    @Inject
    lateinit var sessionStore: SessionStore
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val allSessionsStore: AllSessionsStore by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(AllSessionsStore::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.all_sessions_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        println(allSessionsStore)
    }
}
