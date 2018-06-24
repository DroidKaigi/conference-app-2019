package io.github.droidkaigi.confsched2019.session.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.droidkaigi.confsched2019.R

class AllSessionsFragment : Fragment() {

    companion object {
        fun newInstance() = AllSessionsFragment()
    }

//    private lateinit var viewStore: SessionViewStore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.all_sessions_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        viewStore = ViewModelProviders.of(this).get(SessionViewStore::class.java)
    }

}
