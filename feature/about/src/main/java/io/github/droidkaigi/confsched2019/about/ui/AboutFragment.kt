package io.github.droidkaigi.confsched2019.about.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import dagger.Module
import dagger.Provides
import io.github.droidkaigi.confsched2019.about.R
import io.github.droidkaigi.confsched2019.about.databinding.FragmentAboutBinding
import io.github.droidkaigi.confsched2019.about.ui.item.AboutSection
import io.github.droidkaigi.confsched2019.about.ui.widget.DaggerFragment
import io.github.droidkaigi.confsched2019.about.ui.widget.DottedItemDecoration
import io.github.droidkaigi.confsched2019.di.PageScope
import javax.inject.Inject

class AboutFragment : DaggerFragment() {

    private lateinit var binding: FragmentAboutBinding
    @Inject lateinit var aboutSection: AboutSection

    @Inject lateinit var activityActionCreator: ActivityActionCreator

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val groupAdapter = GroupAdapter<ViewHolder>().apply {
            add(aboutSection)
        }
        binding.aboutRecycler.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = groupAdapter
            addItemDecoration(
                DottedItemDecoration.from(
                    context = context,
                    colorRes = R.color.gray3,
                    paddingLeftRes = R.dimen.divider_padding,
                    paddingRightRes = R.dimen.divider_padding,
                    widthRes = R.dimen.divider_width,
                    gapRes = R.dimen.divider_gap
                )
            )
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
        aboutSection.setupAboutThisApps() {
            activityActionCreator.openUrl(it)
        }
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
