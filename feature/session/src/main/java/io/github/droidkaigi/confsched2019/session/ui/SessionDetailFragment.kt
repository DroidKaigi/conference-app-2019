package io.github.droidkaigi.confsched2019.session.ui

import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.text.inSpans
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionManager
import com.soywiz.klock.DateTimeSpan
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.databinding.ViewHolder
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.di.PageScope
import io.github.droidkaigi.confsched2019.ext.android.afterMeasured
import io.github.droidkaigi.confsched2019.ext.android.changed
import io.github.droidkaigi.confsched2019.model.LoadingState
import io.github.droidkaigi.confsched2019.model.ServiceSession
import io.github.droidkaigi.confsched2019.model.SpeechSession
import io.github.droidkaigi.confsched2019.model.defaultLang
import io.github.droidkaigi.confsched2019.session.R
import io.github.droidkaigi.confsched2019.session.databinding.FragmentSessionDetailBinding
import io.github.droidkaigi.confsched2019.session.ui.actioncreator.SessionContentsActionCreator
import io.github.droidkaigi.confsched2019.session.ui.item.SpeakerItem
import io.github.droidkaigi.confsched2019.session.ui.store.SessionContentsStore
import io.github.droidkaigi.confsched2019.session.ui.widget.DaggerFragment
import io.github.droidkaigi.confsched2019.session.ui.widget.SessionToolbarBehavior
import io.github.droidkaigi.confsched2019.system.actioncreator.ActivityActionCreator
import io.github.droidkaigi.confsched2019.user.store.UserStore
import io.github.droidkaigi.confsched2019.util.ProgressTimeLatch
import javax.inject.Inject

class SessionDetailFragment : DaggerFragment() {
    private lateinit var binding: FragmentSessionDetailBinding

    @Inject lateinit var sessionContentsActionCreator: SessionContentsActionCreator
    @Inject lateinit var sessionContentsStore: SessionContentsStore
    @Inject lateinit var userStore: UserStore
    @Inject lateinit var speakerItemFactory: SpeakerItem.Factory
    @Inject lateinit var activityActionCreator: ActivityActionCreator
    @Inject lateinit var navController: NavController

    private lateinit var progressTimeLatch: ProgressTimeLatch

    private lateinit var sessionDetailFragmentArgs: SessionDetailFragmentArgs
    private var showEllipsis = true
    private val ellipsisLineCount = 5

    private val groupAdapter
        get() = binding.sessionSpeakers.adapter as GroupAdapter<*>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_session_detail,
            container,
            false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sessionDetailFragmentArgs = SessionDetailFragmentArgs.fromBundle(arguments ?: Bundle())

        val groupAdapter = GroupAdapter<ViewHolder<*>>()
        binding.sessionSpeakers.adapter = groupAdapter

        binding.sessionToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.bottomAppBar.replaceMenu(R.menu.menu_session_detail_bottomappbar)
        binding.bottomAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.session_share -> {
                    val session = binding.session ?: return@setOnMenuItemClickListener false
                    activityActionCreator.shareUrl(
                        getString(
                            R.string.session_detail_share_url,
                            session.id
                        )
                    )
                }
                R.id.session_place -> {
                    navController.navigate(
                        SessionDetailFragmentDirections.actionSessionDetailToFloormap()
                    )
                }
            }
            return@setOnMenuItemClickListener true
        }

        userStore.registered.changed(viewLifecycleOwner) { registered ->
            if (registered && sessionContentsStore.isInitialized) {
                sessionContentsActionCreator.refresh()
            }
        }

        sessionContentsStore.speechSession(sessionDetailFragmentArgs.session)
            .changed(viewLifecycleOwner) { session ->
                applySpeechSessionLayout(session)
            }

        sessionContentsStore.serviceSession(sessionDetailFragmentArgs.session)
            .changed(viewLifecycleOwner) { serviceSession ->
                applyServiceSessionLayout(serviceSession)
            }

        progressTimeLatch = ProgressTimeLatch { showProgress ->
            binding.progressBar.isVisible = showProgress
        }
        sessionContentsStore.loadingState.changed(viewLifecycleOwner) {
            progressTimeLatch.loading = it == LoadingState.LOADING
        }

        binding.sessionFavorite.setOnClickListener {
            val session = binding.session ?: return@setOnClickListener

            // Immediate reflection on view to avoid time lag
            binding.sessionFavorite.setImageResource(
                if (session.isFavorited) {
                    R.drawable.ic_bookmark_border_black_24dp
                } else {
                    R.drawable.ic_bookmark_black_24dp
                }
            )

            // Animation
            it.scaleX = 0.8f
            it.scaleY = 0.8f
            it.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300)
                .interpolator = OvershootInterpolator()

            sessionContentsActionCreator.toggleFavorite(session)
        }

        binding.sessionGoToSurvey.setOnClickListener {
            val session = binding.session as? SpeechSession ?: return@setOnClickListener
            navController.navigate(
                SessionDetailFragmentDirections.actionSessionDetailToSessionSurvey(
                    session
                )
            )
        }
    }

    private fun applySpeechSessionLayout(session: SpeechSession) {
        binding.session = session
        binding.speechSession = session
        val lang = defaultLang()
        binding.lang = lang
        setupSessionDescription(session.desc)
        binding.timeZoneOffset = DateTimeSpan(hours = 9) // FIXME Get from device setting

        binding.sessionTitle.text = session.title.getByLang(lang)

        val timeInMinutes: Int = session.timeInMinutes
        binding.sessionTimeAndRoom.text = getString(
            R.string.session_duration_room_format,
            timeInMinutes,
            session.room.name
        )
        binding.sessionIntendedAudienceDescription.text = session.intendedAudience
        binding.categoryChip.text = session.category.name.getByLang(defaultLang())

        session.message?.let { message ->
            binding.sessionMessage.text = message.getByLang(defaultLang())
        }

        val speakerItems = session
            .speakers
            .map {
                speakerItemFactory.create(
                    it,
                    SessionDetailFragmentDirections.actionSessionDetailToSpeaker(it.id)
                )
            }

        groupAdapter.update(speakerItems)

        binding.sessionVideoButton.setOnClickListener {
            session.videoUrl?.let { urlString ->
                activityActionCreator.openUrl(urlString)
            }
        }
        binding.sessionSlideButton.setOnClickListener {
            session.slideUrl?.let { urlString ->
                activityActionCreator.openUrl(urlString)
            }
        }

        (binding.contentParent.layoutParams as CoordinatorLayout.LayoutParams).behavior =
            SessionToolbarBehavior(
                requireContext(),
                binding.sessionToolbar,
                session.title.getByLang(lang)
            )
        // To setup the toolbar when it's backed from a speaker page.
        binding.scrollView.afterMeasured {
            if (binding.scrollView.scrollY != 0) {
                val resources = context?.resources
                resources?.let {
                    with(binding.sessionToolbar) {
                        elevation = it.getDimension(
                            R.dimen.session_detail_toolbar_elevation_not_top
                        ) / it.displayMetrics.density
                        title = session.title.getByLang(lang)
                    }
                }
            }
        }
    }

    private fun setupSessionDescription(description: String) {
        binding.sessionDescription.doOnPreDraw {
            val expectedLineCount = computeLineCount(description)
            if (expectedLineCount > ellipsisLineCount && showEllipsis) {
                val lineLength = description.length / expectedLineCount
                val end = lineLength * ellipsisLineCount
                val ellipsis = getString(R.string.ellipsis_label)
                val ellipsisColor = ContextCompat.getColor(requireContext(), R.color.colorSecondary)
                val onClickListener = {
                    TransitionManager.beginDelayedTransition(binding.sessionLayout)
                    binding.sessionDescription.text = description
                    showEllipsis = !showEllipsis
                }
                val detailText = description.subSequence(0, end - ellipsis.length)
                val text = buildSpannedString {
                    clickableSpan(onClickListener, {
                        append(detailText)
                        color(ellipsisColor) {
                            append(ellipsis)
                        }
                    })
                }
                binding.sessionDescription.setText(text, TextView.BufferType.SPANNABLE)
                binding.sessionDescription.movementMethod = LinkMovementMethod.getInstance()
            } else {
                binding.sessionDescription.text = description
            }
        }
    }

    private fun SpannableStringBuilder.clickableSpan(
        clickListener: () -> Unit,
        builderAction: SpannableStringBuilder.() -> Unit
    ) {
        inSpans(object : ClickableSpan() {
            override fun onClick(widget: View) {
                clickListener()
            }

            override fun updateDrawState(ds: TextPaint) {
                // nothing
            }
        }, builderAction)
    }

    private fun computeLineCount(fullText: String): Int {
        if (binding.sessionDescription.width == 0) return 0
        val rect = Rect()
        val paint = Paint()
        paint.textSize = binding.sessionDescription.textSize
        paint.getTextBounds(fullText, 0, fullText.length, rect)
        return rect.width() / binding.sessionDescription.width
    }

    private fun applyServiceSessionLayout(session: ServiceSession) {
        binding.session = session
        binding.serviceSession = session

        val lang = defaultLang()
        binding.lang = lang
        binding.timeZoneOffset = DateTimeSpan(hours = 9) // FIXME Get from device setting

        binding.sessionTitle.text = session.title.getByLang(lang)

        val timeInMinutes: Int = session.timeInMinutes
        binding.sessionTimeAndRoom.text = getString(
            R.string.session_duration_room_format,
            timeInMinutes,
            session.room.name
        )

        binding.sessionDescription.text = session.desc
    }
}

@Module
abstract class SessionDetailFragmentModule {

    @Module
    companion object {
        @JvmStatic
        @Provides
        @PageScope
        fun providesLifecycle(sessionsFragment: SessionDetailFragment): Lifecycle {
            return sessionsFragment.viewLifecycleOwner.lifecycle
        }
    }
}
