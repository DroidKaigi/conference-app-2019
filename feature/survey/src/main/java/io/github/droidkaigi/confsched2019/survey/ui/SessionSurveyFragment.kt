package io.github.droidkaigi.confsched2019.survey.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.survey.R
import io.github.droidkaigi.confsched2019.survey.databinding.FragmentSessionSurveyBinding
import io.github.droidkaigi.confsched2019.survey.ui.widget.DaggerFragment

class SessionSurveyFragment : DaggerFragment() {
    private lateinit var binding: FragmentSessionSurveyBinding

    private lateinit var sessionSurveyFragmentArgs: SessionSurveyFragmentArgs

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_session_survey,
            container,
            false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionSurveyFragmentArgs = SessionSurveyFragmentArgs.fromBundle(arguments ?: Bundle())
    }
}

@Module
abstract class SessionSurveyFragmentModule {

    @Module
    companion object {
        @JvmStatic @Provides
fun providesLifecycle(sessionSurveyFragment: SessionSurveyFragment): Lifecycle {
            return sessionSurveyFragment.viewLifecycleOwner.lifecycle
        }
    }
}
