package io.github.droidkaigi.confsched2019.survey.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.ext.android.changed
import io.github.droidkaigi.confsched2019.ext.android.requireValue
import io.github.droidkaigi.confsched2019.model.defaultLang
import io.github.droidkaigi.confsched2019.survey.R
import io.github.droidkaigi.confsched2019.survey.databinding.FragmentSessionSurveyBinding
import io.github.droidkaigi.confsched2019.survey.databinding.ItemSessionSurveyBinding
import io.github.droidkaigi.confsched2019.survey.ui.actioncreator.SessionSurveyActionCreator
import io.github.droidkaigi.confsched2019.survey.ui.store.SessionSurveyStore
import io.github.droidkaigi.confsched2019.survey.ui.widget.DaggerFragment
import io.github.droidkaigi.confsched2019.util.ProgressTimeLatch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.tatarka.injectedvmprovider.InjectedViewModelProviders
import timber.log.Timber
import timber.log.debug
import javax.inject.Inject
import javax.inject.Provider

class SessionSurveyFragment : DaggerFragment() {

    @Inject
    lateinit var sessionSurveyActionCreator: SessionSurveyActionCreator
    @Inject
    lateinit var sessionSurveyStoreProvider: Provider<SessionSurveyStore>
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

        progressTimeLatch = ProgressTimeLatch { showProgress ->
            binding.progressBar.isVisible = showProgress
        }

        sessionSurveyStore.loadingState.changed(viewLifecycleOwner) {
            progressTimeLatch.loading = it.isLoading
        }

        sessionSurveyStore.sessionFeedback.changed(viewLifecycleOwner) { sessionFeedback ->
            Timber.debug { sessionFeedback.toString() }
            // TODO: save sessionFeedback state to cacheDB
        }

        val lang = defaultLang()
        binding.sessionTitle.text = sessionSurveyFragmentArgs.session.title.getByLang(lang)
        // TODO: set speackers icon

        binding.submitButton.setOnClickListener {
            val sessionFeedback =
                sessionSurveyStore.sessionFeedback.requireValue().copy(submitted = true)
            if (sessionFeedback.fillouted) {
                // TODO: show confirm dialog
                sessionSurveyActionCreator.submit(
                    sessionSurveyFragmentArgs.session,
                    sessionFeedback
                )
            } else {
                // TODO: show snackbar no input item message
            }
        }

        setupSessionSurveyPager()
        sessionSurveyActionCreator.load(sessionSurveyFragmentArgs.session.id)
    }

    private fun setupSessionSurveyPager() {
        val surveyItems = resources.getStringArray(R.array.survey_items)
        binding.sessionSurveyViewPager.adapter = object : PagerAdapter() {
            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val itemBindng = DataBindingUtil.inflate<ItemSessionSurveyBinding>(
                    LayoutInflater.from(context),
                    R.layout.item_session_survey,
                    container,
                    true
                )
                itemBindng.title.text = surveyItems[position]

                if (position == 5) {
                    itemBindng.comment.visibility = View.VISIBLE
                    itemBindng.rating.visibility = View.GONE
                    itemBindng.comment.setText(
                        sessionSurveyStore.comment,
                        TextView.BufferType.EDITABLE
                    )
                } else {
                    itemBindng.comment.visibility = View.GONE
                    itemBindng.rating.visibility = View.VISIBLE
                    when (position) {
                        0 -> itemBindng.rating.rating = sessionSurveyStore.totalEvaluation.toFloat()
                        1 -> itemBindng.rating.rating = sessionSurveyStore.relevancy.toFloat()
                        2 -> itemBindng.rating.rating = sessionSurveyStore.asExpected.toFloat()
                        3 -> itemBindng.rating.rating = sessionSurveyStore.difficulty.toFloat()
                        4 -> itemBindng.rating.rating = sessionSurveyStore.knowledgeable.toFloat()
                    }
                }

                itemBindng.rating.setOnRatingBarChangeListener { _, rating, _ ->
                    val newSessionFeedback = when (position) {
                        0 -> {
                            sessionSurveyStore.sessionFeedback.requireValue()
                                .copy(totalEvaluation = rating.toInt())
                        }
                        1 -> {
                            sessionSurveyStore.sessionFeedback.requireValue()
                                .copy(relevancy = rating.toInt())
                        }
                        2 -> {
                            sessionSurveyStore.sessionFeedback.requireValue()
                                .copy(asExpected = rating.toInt())
                        }
                        3 -> {
                            sessionSurveyStore.sessionFeedback.requireValue()
                                .copy(difficulty = rating.toInt())
                        }
                        4 -> {
                            sessionSurveyStore.sessionFeedback.requireValue()
                                .copy(knowledgeable = rating.toInt())
                        }
                        else -> sessionSurveyStore.sessionFeedback.requireValue()
                    }

                    sessionSurveyActionCreator.changeSessionFeedback(newSessionFeedback)
                    GlobalScope.launch(Dispatchers.Main) {
                        // Late transition a little to improve the experience
                        delay(300)
                        binding.sessionSurveyViewPager.setCurrentItem(position + 1, true)
                    }
                }

                itemBindng.comment.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        val newSessionFeedback = sessionSurveyStore.sessionFeedback.requireValue()
                            .copy(comment = s.toString())
                        sessionSurveyActionCreator.changeSessionFeedback(newSessionFeedback)
                    }
                })

                return itemBindng.root
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                container.removeView(`object` as View)
            }

            override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

            override fun getCount(): Int = 6
        }

        binding.sessionSurveyViewPager.addOnPageChangeListener(object :
            ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                binding.submitButton.isVisible = position == 5
            }
        })
    }
}

@Module
abstract class SessionSurveyFragmentModule {

    @Module
    companion object {
        @JvmStatic
        @Provides
        @PageScope
        fun providesLifecycle(sessionSurveyFragment: SessionSurveyFragment): Lifecycle {
            return sessionSurveyFragment.viewLifecycleOwner.lifecycle
        }
    }
}
