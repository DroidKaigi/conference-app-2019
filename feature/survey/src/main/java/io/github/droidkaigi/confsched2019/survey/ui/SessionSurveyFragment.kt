package io.github.droidkaigi.confsched2019.survey.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.viewpager.widget.PagerAdapter
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.ext.android.changed
import io.github.droidkaigi.confsched2019.ext.android.requireValue
import io.github.droidkaigi.confsched2019.survey.R
import io.github.droidkaigi.confsched2019.survey.databinding.FragmentSessionSurveyBinding
import io.github.droidkaigi.confsched2019.survey.databinding.ItemSessionSurveyBinding
import io.github.droidkaigi.confsched2019.survey.ui.actioncreator.SessionSurveyActionCreator
import io.github.droidkaigi.confsched2019.survey.ui.store.SessionSurveyStore
import io.github.droidkaigi.confsched2019.survey.ui.widget.DaggerFragment
import io.github.droidkaigi.confsched2019.util.ProgressTimeLatch
import me.tatarka.injectedvmprovider.InjectedViewModelProviders
import javax.inject.Inject
import javax.inject.Provider

class SessionSurveyFragment : DaggerFragment() {

    @Inject lateinit var sessionSurveyActionCreator: SessionSurveyActionCreator
    @Inject lateinit var sessionSurveyStoreProvider: Provider<SessionSurveyStore>
    private val sessionSurveyStore: SessionSurveyStore by lazy {
        InjectedViewModelProviders.of(requireActivity())[sessionSurveyStoreProvider]
    }

    private lateinit var binding: FragmentSessionSurveyBinding
    private lateinit var progressTimeLatch: ProgressTimeLatch

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

        sessionSurveyStore.loadingState.changed(viewLifecycleOwner) {
            progressTimeLatch.loading = it.isLoading
        }

        sessionSurveyStore.sessionFeedback.changed(viewLifecycleOwner) { sessionFeedback ->
            // TODO: save sessionFeedback state to cacheDB
        }

        binding.submitButton.setOnClickListener {
            val sessionFeedback = sessionSurveyStore.sessionFeedback.requireValue()
            if (sessionFeedback.fillouted) {
                sessionSurveyActionCreator.submit(
                    sessionSurveyFragmentArgs.session,
                    sessionFeedback
                )
            }
        }

        setupSessionSurveyPager()
        sessionSurveyActionCreator.load(sessionSurveyFragmentArgs.session.id)
    }

    private fun setupSessionSurveyPager() {
        binding.sessionSurveyViewPager.adapter = object : PagerAdapter() {
            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val itemBindng = DataBindingUtil.inflate<ItemSessionSurveyBinding>(
                    LayoutInflater.from(context),
                    R.layout.item_session_survey,
                    container,
                    true
                )
                itemBindng.title.text = ""
                itemBindng.rating.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
                    val newSessionFeedback = sessionSurveyStore.sessionFeedback
                        .requireValue()
                        .copy()
                    sessionSurveyActionCreator.changeSessionFeedback(newSessionFeedback)
                }

                return itemBindng.root
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                container.removeView(`object` as View)
            }

            override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

            override fun getCount(): Int = 6
        }
    }
}

@Module
abstract class SessionSurveyFragmentModule {

    @Module
    companion object {
        @JvmStatic @Provides
        @PageScope
        fun providesLifecycle(sessionSurveyFragment: SessionSurveyFragment): Lifecycle {
            return sessionSurveyFragment.viewLifecycleOwner.lifecycle
        }
    }
}
