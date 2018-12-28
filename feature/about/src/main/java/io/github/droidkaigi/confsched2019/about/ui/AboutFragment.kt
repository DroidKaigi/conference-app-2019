package io.github.droidkaigi.confsched2019.about.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.about.R
import io.github.droidkaigi.confsched2019.about.databinding.FragmentAboutBinding
import io.github.droidkaigi.confsched2019.about.ui.widget.DaggerFragment
import io.github.droidkaigi.confsched2019.di.PageScope

class AboutFragment : DaggerFragment() {

    private lateinit var binding: FragmentAboutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_about,
            container,
            false
        )
        return binding.root
    }
}

@Module
abstract class AboutFragmentModule {
    @Module
    companion object {
        @PageScope @JvmStatic @Provides fun providesLifecycle(
            aboutFragment: AboutFragment
        ): Lifecycle {
            return aboutFragment.viewLifecycleOwner.lifecycle
        }
    }
}
