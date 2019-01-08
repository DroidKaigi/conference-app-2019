package io.github.droidkaigi.confsched2019.about.ui.item

import android.view.View
import androidx.databinding.DataBindingUtil
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.databinding.ViewHolder
import io.github.droidkaigi.confsched2019.about.R
import io.github.droidkaigi.confsched2019.about.databinding.HeaderAboutBinding

class AboutHeaderItem(
    private val twitterUrl: String,
    private val githubUrl: String,
    private val youtubeUrl: String,
    private val mediumUrl: String,
    private val openUrl: ((String) -> Unit)
) : BindableItem<HeaderAboutBinding>() {

    override fun createViewHolder(itemView: View): ViewHolder<HeaderAboutBinding> {
        return ViewHolder(DataBindingUtil.bind(itemView)!!)
    }

    override fun getLayout(): Int = R.layout.header_about

    // TODO: Set banner image/background
    override fun bind(binding: HeaderAboutBinding, position: Int) {
        binding.aboutThisAppTwitter.setOnClickListener { openUrl(twitterUrl) }
        binding.aboutThisAppGithub.setOnClickListener { openUrl(githubUrl) }
        binding.aboutThisAppYoutube.setOnClickListener { openUrl(youtubeUrl) }
        binding.aboutThisAppMedium.setOnClickListener { openUrl(mediumUrl) }
    }
}
