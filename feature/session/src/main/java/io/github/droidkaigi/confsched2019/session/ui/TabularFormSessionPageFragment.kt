package io.github.droidkaigi.confsched2019.session.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.FragmentTabularFormSessionPageBinding
import io.github.droidkaigi.confsched2019.session.di.SessionPageScope
import io.github.droidkaigi.confsched2019.session.ui.widget.DaggerFragment

class TabularFormSessionPageFragment : DaggerFragment() {

    private lateinit var binding: FragmentTabularFormSessionPageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_tabular_form_session_page,
            container,
            false
        )
        return binding.root
    }

    companion object {
        fun newInstance(): TabularFormSessionPageFragment {
            return TabularFormSessionPageFragment()
        }
    }
}

@Module
abstract class TabularFormSessionPageFragmentModule {

    @Module
    companion object {
        @JvmStatic @Provides
        @SessionPageScope
        fun providesLifecycle(
            tabularFromSessionPageFragment: TabularFormSessionPageFragment
        ): Lifecycle {
            return tabularFromSessionPageFragment.viewLifecycleOwner.lifecycle
        }
    }
}
